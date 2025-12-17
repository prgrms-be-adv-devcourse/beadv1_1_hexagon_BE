package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.infra.client.internal.ContractClient;
import com.example.cartpostservice.commissions.infra.client.internal.FileManagementClient;
import com.example.cartpostservice.commissions.infra.client.internal.MemberClient;
import com.example.cartpostservice.commissions.infra.client.internal.dto.request.DownloadFileComponentRequest;
import com.example.cartpostservice.commissions.infra.client.internal.dto.request.FilesRequestDto;
import com.example.cartpostservice.commissions.infra.client.internal.dto.request.TotalPeopleInfoRequestDto;
import com.example.cartpostservice.commissions.infra.client.internal.dto.response.DownloadFileComponentResponse;
import com.example.cartpostservice.commissions.infra.client.internal.dto.response.InternalMemberInfo;
import com.example.cartpostservice.commissions.infra.client.internal.dto.response.MemberInfoOutput;
import com.example.cartpostservice.commissions.infra.client.internal.dto.response.PeopleInfoResponseDto;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionInternalInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.result.DownloadFileComponentsResult;
import com.example.cartpostservice.commissions.service.usecase.result.RecruitsInfoResult;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import com.example.cartpostservice.common.exception.ExceptionLogService;
import com.example.cartpostservice.common.exception.ExternalServerException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternalService {

    private final MemberClient memberClient;
    private final ContractClient contractClient;
    private final FileManagementClient fileManagementClient;
    private final ExceptionLogService exceptionLogService;

    public String getUserNickName(String memberCode) {
        String nickName;
        List<String> codes = List.of(memberCode);

        ResponseDto<MemberInfoOutput> memberClientResponse = memberClient.getMemberInfoByCode(codes);

        if (memberClientResponse == null) {
            throw new ExternalServerException(CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR, "응답 없음");
        }

        nickName = memberClientResponse.data().internalMemberInfos().stream()
                .filter(info -> info.memberCode().equals(memberCode)) // 혹시 모를 다른 회원 데이터 섞임 방지
                .findFirst()
                .map(InternalMemberInfo::nickName) // record 접근자
                .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_MEMBER_INFO));

        return nickName;
    }

    public void saveFileAndRecruitsInfo(CommissionInternalInfoCommand internalInfoCommand) {
        // 파일 저장 s3 모듈 요청 및 사람 인원 정보 저장 계약 모듈 요청
        try {
            sendPeopleInfo(internalInfoCommand.commissionCode(), internalInfoCommand.plannedHires(),
                    internalInfoCommand.eligibleApplicants());

            sendFileKeyComponents(internalInfoCommand.commissionCode(), internalInfoCommand.fileKeys());
        } catch (Exception ex) {
            exceptionLogService.logExternalServerException(ex, "InternalService.saveFileAndRecruitsInfo");

            throw new ExternalServerException(CustomStatusCode.INTERNAL_SERVER_ERROR,
                    "feign client 오류로 인한 롤백 수행 요청합니다");
        }
    }

    public RecruitsInfoResult readRecruitsInfo(String commissionCode) {
        try {
            PeopleInfoResponseDto peopleInfoResponseDto = getPeopleInfo(commissionCode);

            return RecruitsInfoResult.from(peopleInfoResponseDto);
        } catch (Exception e) {
            exceptionLogService.logExternalServerException(e, "InternalService.readRecruitsInfo");

            return null;
        }
    }

    public DownloadFileComponentsResult readFileComponents(String commissionCode) {
        DownloadFileComponentResponse fileComponentResponse = getDownloadFileComponent(commissionCode);

        return new DownloadFileComponentsResult(fileComponentResponse.urls());
    }

    public void updateCommissionInternalInfo(CommissionInternalInfoCommand internalInfoCommand,
            RecruitsInfoResult originalInfo) {

        int sendPlannedHires = originalInfo.plannedHires();
        int sendEligibleApplicants = originalInfo.eligibleApplicants();

        if (internalInfoCommand.plannedHires() != null) {
            sendPlannedHires = internalInfoCommand.plannedHires();
        }

        if (internalInfoCommand.eligibleApplicants() != null) {
            sendEligibleApplicants = internalInfoCommand.eligibleApplicants();
        }

        sendPeopleInfo(internalInfoCommand.commissionCode(), sendPlannedHires,
                sendEligibleApplicants);

        try {
            updateFileKeyComponents(internalInfoCommand.commissionCode(), internalInfoCommand.fileKeys());
        } catch (Exception ex) {
            exceptionLogService.logExternalServerException(ex, "InternalService.updateCommissionInternalInfo");

            sendPeopleInfo(internalInfoCommand.commissionCode(), originalInfo.plannedHires(),
                    originalInfo.eligibleApplicants());
        }
    }

    private void sendPeopleInfo(String commissionCode, Integer plannedHires, Integer eligibleApplicants) {
        TotalPeopleInfoRequestDto totalPeopleInfoRequestDto = new TotalPeopleInfoRequestDto(commissionCode,
                plannedHires, eligibleApplicants);
        ResponseDto<Empty> contractClientResponse = contractClient.upsertNumberOfPeople(totalPeopleInfoRequestDto);

        if (contractClientResponse == null) {
            throw new ExternalServerException(CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR, "응답이 존재하지 않습니다");
        }
    }

    private void sendFileKeyComponents(String commissionCode, List<String> fileKeys) {
        if (fileKeys != null) {
            FilesRequestDto filesRequestDto = new FilesRequestDto(commissionCode, fileKeys);
            ResponseDto<Empty> s3RegisterResponse = fileManagementClient.registerFileStatus(filesRequestDto);

            if (s3RegisterResponse == null) {
                throw new ExternalServerException(CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR, "응답이 존재하지 않습니다");
            }
        }
    }

    private PeopleInfoResponseDto getPeopleInfo(String commissionCode) {
        ResponseDto<PeopleInfoResponseDto> applicantsResponse = contractClient.getNumberOfPeople(commissionCode);

        if (applicantsResponse == null) {
            throw new ExternalServerException(CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR, "응답 없음");
        }

        return applicantsResponse.data();
    }

    private DownloadFileComponentResponse getDownloadFileComponent(String commissionCode) {
        DownloadFileComponentRequest downloadFileComponentRequest = new DownloadFileComponentRequest(commissionCode);
        ResponseDto<DownloadFileComponentResponse> downloadFileComponents = fileManagementClient.getDownloadFileComponent(
                downloadFileComponentRequest);

        return downloadFileComponents.data();
    }

    private void updateFileKeyComponents(String commissionCode, List<String> fileKeys) {
        if (fileKeys != null) {
            FilesRequestDto filesRequestDto = new FilesRequestDto(commissionCode, fileKeys);

            ResponseDto<Empty> s3UpdateResponse = fileManagementClient.updateFileStatus(filesRequestDto);

            if (s3UpdateResponse == null) {
                throw new ExternalServerException(CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR, "응답이 존재하지 않습니다");
            }
        }
    }


}
