package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.controller.dto.request.CommissionCreateRequest;
import com.example.cartpostservice.commissions.controller.dto.request.CommissionUpdateRequest;
import com.example.cartpostservice.commissions.controller.dto.request.internal.DownloadFileComponentRequest;
import com.example.cartpostservice.commissions.controller.dto.request.internal.FilesRequestDto;
import com.example.cartpostservice.commissions.controller.dto.request.internal.TotalPeopleInfoRequestDto;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionCreateResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionElementReadResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionUpdateResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionReadResponse;
import com.example.cartpostservice.commissions.controller.dto.response.internal.DownloadFileComponentResponse;
import com.example.cartpostservice.commissions.controller.dto.response.internal.InternalMemberInfo;
import com.example.cartpostservice.commissions.controller.dto.response.internal.MemberInfoOutput;
import com.example.cartpostservice.commissions.controller.dto.response.internal.PeopleInfoResponseDto;
import com.example.cartpostservice.commissions.controller.dto.response.internal.PresignedUrlComponent;
import com.example.cartpostservice.commissions.controller.internal.ContractClient;
import com.example.cartpostservice.commissions.controller.internal.FileManagementClient;
import com.example.cartpostservice.commissions.controller.internal.MemberClient;
import com.example.cartpostservice.commissions.service.dto.request.CommissionsServiceCommand;
import com.example.cartpostservice.commissions.service.dto.request.TagServiceCommand;
import com.example.cartpostservice.commissions.service.dto.response.CommissionsServiceResult;
import com.example.cartpostservice.commissions.service.dto.response.TagServiceResult;
import com.example.cartpostservice.commissions.service.kafka.CommissionKafkaService;
import com.example.cartpostservice.commissions.service.kafka.dto.request.CommissionServiceMessage;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import com.example.cartpostservice.common.exception.ExternalServerException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.vo.ServiceName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommissionsManagerService {

    private final CommissionsService commissionsService;
    private final CommissionsTagService commissionsTagService;
    private final CommissionKafkaService commissionKafkaService;
    private final MemberClient memberClient;
    private final ContractClient contractClient;
    private final FileManagementClient fileManagementClient;

    @Transactional
    public CommissionCreateResponse createCommission(String memberCode, CommissionCreateRequest request) {

        String nickName = "";
        try{
            List<String> codes = List.of(memberCode);

            ResponseDto<MemberInfoOutput> memberClientResponse = memberClient.getMemberInfoByCode(codes);

            if(memberClientResponse == null){
                throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, "응답 없음");
            }

            nickName = memberClientResponse.data().internalMemberInfos().stream()
                    .filter(info -> info.memberCode().equals(memberCode)) // 혹시 모를 다른 회원 데이터 섞임 방지
                    .findFirst()
                    .map(InternalMemberInfo::nickName) // record 접근자
                    .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_MEMBER_INFO));

        } catch (Exception e) {
            log.error("[MemberService 연동 실패] 기본값으로 저장합니다. 추후 동기화 필요. 대상: {}, 원인: {}",
                    memberCode, e.getMessage(), e);

            // 사용자용 처리: 서비스를 멈추지 않고 기본값 할당
            nickName = "사용자";
        }

        // request에서 온 것을 커미션과 태그 용 리퀘스트로 분리
        CommissionsServiceCommand commissionsServiceCommand = new CommissionsServiceCommand(
                memberCode,
                request.title(),
                request.content(),
                request.paymentType(),
                request.unitAmount(),
                request.startedAt(),
                request.endedAt(),
                nickName
        );

        // 커미션 서비스에 리퀘스트 데이터를 저장 데이터 받기
        String commissionCode = commissionsService.create(commissionsServiceCommand);

        // 태그 서비스에 리퀘스트 데이터 저장
        TagServiceCommand tagServiceCommand = new TagServiceCommand(
                commissionCode,
                request.tagCode()
        );

        commissionsTagService.create(tagServiceCommand);

        // 응답 데이터에 commissionscode 전달
        CommissionCreateResponse commissionCreateResponse = new CommissionCreateResponse(commissionCode);

        sendContractInfo(commissionCode, request.plannedHires(), request.eligibleApplicants());

        // s3 모듈에게 파일 저장 요청
        if(request.fileKeys() != null){
            FilesRequestDto filesRequestDto = new FilesRequestDto(commissionCode,request.fileKeys());
            ResponseDto<Empty> s3RegisterResponse = fileManagementClient.registerFileStatus(filesRequestDto);

            if(s3RegisterResponse == null){
                throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, "응답 없음");
            }
        }

        // kafka
        CommissionsServiceResult commissionResult = commissionsService.read(commissionCode);
        CommissionServiceMessage createMessage = new CommissionServiceMessage(
                commissionCode,
                commissionResult.title(),
                commissionResult.content(),
                commissionResult.memberCode(),
                commissionResult.writerName(),
                request.tagCode(),
                commissionResult.startedAt(),
                commissionResult.endedAt(),
                commissionResult.paymentType(),
                Long.parseLong(commissionResult.unitAmount()),
                commissionResult.isOpen(),
                commissionResult.updatedAt()
        );


        commissionKafkaService.createProducer(createMessage);

        return commissionCreateResponse;
    }

    @Transactional
    public CommissionElementReadResponse readCommission(String commissionCode) {

        CommissionsServiceResult commissionResult = commissionsService.read(commissionCode);
        TagServiceResult tagResult = commissionsTagService.read(commissionResult.code());

        ResponseDto<PeopleInfoResponseDto> applicantsResponse = contractClient.getNumberOfPeople(commissionResult.code());

        if(applicantsResponse == null ){
            throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, "응답 없음");
        }

        if(applicantsResponse.httpStatus() != 200){
            throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, applicantsResponse.message());
        }

        PeopleInfoResponseDto peopleInfo = applicantsResponse.data();

        DownloadFileComponentRequest downloadFileComponentRequest = new DownloadFileComponentRequest(ServiceName.COMMISSIONS, commissionCode);
        ResponseDto<DownloadFileComponentResponse> downloadFileComponents = fileManagementClient.getDownloadFileComponent(downloadFileComponentRequest);

        return new CommissionElementReadResponse(
                commissionResult.title(),
                commissionResult.content(),
                commissionResult.paymentType(),
                commissionResult.unitAmount(),
                commissionResult.startedAt(),
                commissionResult.endedAt(),
                commissionResult.isOpen(),
                commissionResult.writerName(),
                tagResult.tagCodes(),
                peopleInfo.applyCapacity(),
                peopleInfo.appliedCount(),
                peopleInfo.selectionCapacity(),
                peopleInfo.selectedCount(),
                downloadFileComponents.data().urls()
        );
    }

    @Transactional
    public CommissionUpdateResponse updateCommission(String code, String commissionCode,
            CommissionUpdateRequest request) {

        CommissionsServiceResult commissionReadResult = commissionsService.read(commissionCode);
        TagServiceResult tagReadResult = commissionsTagService.read(commissionCode);

        CommissionsServiceCommand commissionsServiceCommand = new CommissionsServiceCommand(
                code,
                getOrDefault(request.title(), commissionReadResult.title()),
                getOrDefault(request.content(), commissionReadResult.content()),
                getOrDefault(request.paymentType(), commissionReadResult.paymentType()),
                getOrDefault(request.unitAmount(), commissionReadResult.unitAmount()),
                getOrDefault(request.startedAt(), commissionReadResult.startedAt()),
                getOrDefault(request.endedAt(), commissionReadResult.endedAt()),
                commissionReadResult.writerName()
        );

        TagServiceCommand tagServiceCommand = new TagServiceCommand(
                commissionCode,
                getOrDefault(request.tagCode(), tagReadResult.tagCodes())
        );

        commissionsService.update(commissionsServiceCommand, commissionCode);
        commissionsTagService.update(tagServiceCommand, commissionCode);


        if(request.plannedHires() != null || request.eligibleApplicants() != null){
            sendContractInfo(commissionCode,  request.plannedHires(), request.eligibleApplicants());
        }

        List<String> updatedKeys = request.fileKeys();
        if(request.fileKeys() == null){
            DownloadFileComponentRequest downloadFileComponentRequest = new DownloadFileComponentRequest(ServiceName.COMMISSIONS, commissionCode);
            ResponseDto<DownloadFileComponentResponse> downloadFileComponents = fileManagementClient.getDownloadFileComponent(downloadFileComponentRequest);
            updatedKeys = downloadFileComponents.data().urls().stream()
                    .map(PresignedUrlComponent::key)
                    .toList();
        }

        FilesRequestDto filesRequestDto = new FilesRequestDto(commissionCode, updatedKeys);
        ResponseDto<Empty> updateFileComponents =  fileManagementClient.updateFileStatus(filesRequestDto);

        if(updateFileComponents == null){
            throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, "응답 없음");
        }

        // kafka
        CommissionsServiceResult commissionUpdateResult = commissionsService.read(commissionCode);
        TagServiceResult tagResult = commissionsTagService.read(commissionUpdateResult.code());
        CommissionServiceMessage updateMessage = new CommissionServiceMessage(
                commissionCode,
                commissionUpdateResult.title(),
                commissionUpdateResult.content(),
                commissionUpdateResult.memberCode(),
                commissionUpdateResult.writerName(),
                tagResult.tagCodes(),
                commissionUpdateResult.startedAt(),
                commissionUpdateResult.endedAt(),
                commissionUpdateResult.paymentType(),
                Long.parseLong(commissionUpdateResult.unitAmount()),
                commissionUpdateResult.isOpen(),
                commissionUpdateResult.updatedAt()
        );
        commissionKafkaService.updateProducer(updateMessage);

        return new CommissionUpdateResponse(commissionCode);
    }

    @Transactional
    public void deleteCommission(String code, String commissionCode) {

        commissionsService.delete(code, commissionCode);
        commissionsTagService.delete(code, commissionCode);

        // kafka
        commissionKafkaService.deleteProducer(commissionCode);
    }

    @Transactional
    public void finishCommission(String code, String commissionCode) {
        if (!commissionsService.isOwner(code, commissionCode)) {
            throw new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION);
        }

        commissionsService.closeCommission(commissionCode);

        CommissionsServiceResult commissionResult = commissionsService.read(commissionCode);
        TagServiceResult tagResult = commissionsTagService.read(commissionResult.code());

        CommissionServiceMessage finishMessage = new CommissionServiceMessage(
                commissionCode,
                commissionResult.title(),
                commissionResult.content(),
                commissionResult.memberCode(),
                commissionResult.writerName(),
                tagResult.tagCodes(),
                commissionResult.startedAt(),
                commissionResult.endedAt(),
                commissionResult.paymentType(),
                Long.parseLong(commissionResult.unitAmount()),
                commissionResult.isOpen(),
                commissionResult.updatedAt()
        );

        // kafka
        commissionKafkaService.finishProducer(finishMessage);

    }

    @Transactional
    public Page<CommissionReadResponse> readOwnCommissions(String code, Pageable pageable) {

        int page = 0;
        if (pageable.getPageNumber() > 0) {
            page = pageable.getPageNumber() - 1;
        }

        Pageable adjustedPageable = PageRequest.of(
                page,
                pageable.getPageSize(),
                pageable.getSort()
        );

        Page<CommissionsServiceResult> resultPage = commissionsService.getPage(code, adjustedPageable);

        List<String> commissionCodes = resultPage.stream()
                .map(CommissionsServiceResult::code)
                .toList();

        List<TagServiceResult> tagServiceResults = commissionsTagService.getTags(commissionCodes);

        Map<String, List<String>> tagMap = tagServiceResults.stream()
                .collect(Collectors.toMap(
                        TagServiceResult::commissionCode,
                        TagServiceResult::tagCodes
                ));

        List<CommissionReadResponse> responses = resultPage.stream()
                .map(result -> new CommissionReadResponse(
                        result.title(),
                        result.paymentType(),
                        result.unitAmount(),
                        result.startedAt(),
                        result.endedAt(),
                        result.isOpen(),
                        result.writerName(),
                        tagMap.getOrDefault(result.code(), List.of())
                ))
                .toList();

        return new PageImpl<>(responses, pageable, resultPage.getTotalElements());
    }

    @Transactional
    public void canAccessCommission(String code, String commissionCode) {
        if (!commissionsService.isOwner(code, commissionCode)) {
            throw new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION);
        }
    }

    private void sendContractInfo(String commissionCode, Integer plannedHires, Integer eligibleApplicants) {
        TotalPeopleInfoRequestDto totalPeopleInfoRequestDto = new TotalPeopleInfoRequestDto(commissionCode, plannedHires, eligibleApplicants);
        ResponseDto<Empty> contractClientResponse = contractClient.upsertNumberOfPeople(totalPeopleInfoRequestDto);

        if(contractClientResponse == null ){
            throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, "응답 없음");
        }

        if(contractClientResponse.httpStatus() != 201){
            throw new ExternalServerException(CustomStatusCode.EXTERNAL_SERVER_ERROR, contractClientResponse.message());
        }
    }

    private <T> T getOrDefault(T newValue, T oldValue) {
        return newValue != null ? newValue : oldValue;
    }
}
