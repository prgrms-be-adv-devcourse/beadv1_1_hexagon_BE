package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionCreateRequest;
import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionUpdateRequest;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionCreateResponse;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionReadResponse;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionUpdateResponse;
import com.example.cartpostservice.commissions.controller.internal.dto.response.CommissionRecruitmentStatusResponse;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionAndTagPartitionInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheCreatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheUpdatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionInternalInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionTotalInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionUpdatedCommand;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionAndTagReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionElementResult;
import com.example.cartpostservice.commissions.service.usecase.result.DownloadFileComponentsResult;
import com.example.cartpostservice.commissions.service.usecase.result.FileElementResult;
import com.example.cartpostservice.commissions.service.usecase.result.RecruitsInfoResult;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import com.example.cartpostservice.common.exception.ExceptionLogService;
import com.example.cartpostservice.common.exception.ExternalServerException;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestrationService {

    private final InternalService internalService;
    private final DomainCompositeService domainCompositeService;
    private final ExceptionLogService exceptionLogService;

    public CommissionCreateResponse createCommission(String memberCode, CommissionCreateRequest request) {

        // 닉네임 가져올 때 오류 예외 처리 관리
        String nickName = internalService.getUserNickName(memberCode);

        // 이 안에서 하나의 트랜잭션으로 관리
        String commissionCode = domainCompositeService.createCommission(
                CommissionTotalInfoCommand.from(memberCode, nickName, request));

        CommissionAndTagPartitionInfoCommand partitionInfoCommand = CommissionAndTagPartitionInfoCommand.from(
                domainCompositeService.readCommission(commissionCode));

        try {
            internalService.saveFileAndRecruitsInfo(CommissionInternalInfoCommand.from(commissionCode, request));

            domainCompositeService.createCacheInfo(CommissionCacheCreatedCommand.from(commissionCode, request),
                    partitionInfoCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
            domainCompositeService.hardDeleteCommission(commissionCode, memberCode);

            throw new ExternalServerException(CustomStatusCode.INTERNAL_SERVER_ERROR,
                    CustomStatusCode.INTERNAL_SERVER_ERROR.getMessage());
        }
        return new CommissionCreateResponse(commissionCode);
    }

    public CommissionElementResult readCommission(String commissionCode) {
        CommissionAndTagReadResult commissionAndTagReadResult = domainCompositeService.readCommission(commissionCode);
        RecruitsInfoResult recruitsInfoResult = null;

        if (commissionAndTagReadResult.lastSyncTime() == null
                || Duration.between(commissionAndTagReadResult.lastSyncTime(), Instant.now()).getSeconds() > 10) {
            recruitsInfoResult = internalService.readRecruitsInfo(commissionCode);

            if (recruitsInfoResult != null) {
                CommissionCacheUpdatedCommand updatedCommand = CommissionCacheUpdatedCommand.from(recruitsInfoResult,
                        commissionCode);

                domainCompositeService.updateCacheInfo(updatedCommand, CommissionAndTagPartitionInfoCommand.from(
                        domainCompositeService.readCommission(commissionCode)));
            }
        }

        return new CommissionElementResult(commissionAndTagReadResult, recruitsInfoResult);
    }

    public FileElementResult readFileComponents(String commissionCode) {
        DownloadFileComponentsResult componentsResult = internalService.readFileComponents(commissionCode);

        return new FileElementResult(componentsResult.urls());
    }

    public Page<CommissionReadResponse> readOwnCommissions(String memberCode, Pageable pageable) {
        return domainCompositeService.readOwnCommissions(memberCode, pageable);
    }

    public CommissionUpdateResponse updateCommission(String memberCode, String commissionCode,
            CommissionUpdateRequest request) {

        RecruitsInfoResult originalInfo = internalService.readRecruitsInfo(commissionCode);

        if (originalInfo == null) {
            throw new ExternalServerException(CustomStatusCode.INTERNAL_SERVER_ERROR,
                    CustomStatusCode.INTERNAL_SERVER_ERROR.getMessage());
        }

        internalService.updateCommissionInternalInfo(CommissionInternalInfoCommand.from(commissionCode, request),
                originalInfo);

        try {
            domainCompositeService.updateCommission(CommissionUpdatedCommand.from(commissionCode, request));
        } catch (Exception e) {
            exceptionLogService.logExternalServerException(e, "domainCompositeService.updateCommission");
        }

        return new CommissionUpdateResponse(commissionCode);
    }

    public void softDeleteCommission(String memberCode, String commissionCode) {
        // soft delete 이므로 commission에 대해서만 isdeleted 상태만 바꾸고 다른 데이터들은 삭제하지 않는다

        domainCompositeService.softDeleteCommission(memberCode, commissionCode);
    }

    public void finishCommission(String code, String commissionCode) {

    }

    public void openCommission(String memberCode, String commissionCode) {

    }

    public void canAccessCommission(String code, String commissionCode) {

    }

    public CommissionRecruitmentStatusResponse getRecruitmentStatus(String commissionCode) {
        return null;
    }


}
