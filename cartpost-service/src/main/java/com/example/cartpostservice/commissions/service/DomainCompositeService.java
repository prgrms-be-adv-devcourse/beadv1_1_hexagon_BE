package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionUpdateRequest;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionReadResponse;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionUpdateResponse;
import com.example.cartpostservice.commissions.controller.internal.dto.response.CommissionRecruitmentStatusResponse;
import com.example.cartpostservice.commissions.infra.client.internal.ContractClient;
import com.example.cartpostservice.commissions.infra.client.internal.FileManagementClient;
import com.example.cartpostservice.commissions.infra.client.internal.MemberClient;
import com.example.cartpostservice.commissions.infra.kafka.publisher.KafkaCommissionEventPublisher;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.service.event.CommissionDeleteEventFactory;
import com.example.cartpostservice.commissions.service.event.CommissionUpsertEventFactory;
import com.example.cartpostservice.commissions.service.kafka.dto.request.CommissionServiceMessage;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionAndTagPartitionInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheCreatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheUpdatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionTotalInfoCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionsServiceCommand;
import com.example.cartpostservice.commissions.service.usecase.command.TagServiceCommand;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionAndTagReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.TagsReadResult;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DomainCompositeService {

    private final CommissionsService commissionsService;
    private final CommissionsTagService commissionsTagService;
    private final KafkaCommissionEventPublisher commissionKafkaService;
    private final MemberClient memberClient;
    private final ContractClient contractClient;
    private final FileManagementClient fileManagementClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public String createCommission(CommissionTotalInfoCommand commissionTotalInfoCommand) {

        // 커미션 서비스에 리퀘스트 데이터를 저장 데이터 받기
        String commissionCode = commissionsService.create(CommissionsServiceCommand.from(commissionTotalInfoCommand));

        commissionsTagService.create(TagServiceCommand.from(commissionCode, commissionTotalInfoCommand));

        return commissionCode;
    }

    @Transactional
    public void createCacheInfo(CommissionCacheCreatedCommand cacheCommand,
            CommissionAndTagPartitionInfoCommand partitionInfoCommand) {

        commissionsService.createCacheInfo(cacheCommand);

        applicationEventPublisher.publishEvent(CommissionUpsertEventFactory.createEvent(partitionInfoCommand));
    }

    @Transactional
    public CommissionAndTagReadResult readCommission(String commissionCode) {

        CommissionReadResult commissionResult = commissionsService.read(commissionCode);
        TagsReadResult tagResult = commissionsTagService.read(commissionResult.code());

        return CommissionAndTagReadResult.from(commissionResult, tagResult);
    }

    @Transactional
    public void updateCacheInfo(CommissionCacheUpdatedCommand cacheCommand,
            CommissionAndTagPartitionInfoCommand partitionInfoCommand) {

        commissionsService.updateCacheInfo(cacheCommand);

        applicationEventPublisher.publishEvent(CommissionUpsertEventFactory.createEvent(partitionInfoCommand));
    }

    @Transactional
    public CommissionUpdateResponse updateCommission(String code, String commissionCode,
            CommissionUpdateRequest request) {

        CommissionReadResult commissionReadResult = commissionsService.read(commissionCode);
        TagsReadResult tagReadResult = commissionsTagService.read(commissionCode);

//        CommissionsServiceCommand commissionsServiceCommand = new CommissionsServiceCommand(
//                code,
//                getOrDefault(request.title(), commissionReadResult.title()),
//                getOrDefault(request.content(), commissionReadResult.content()),
//                getOrDefault(request.paymentType(), commissionReadResult.paymentType()),
//                getOrDefault(request.unitAmount(), commissionReadResult.unitAmount()),
//                getOrDefault(request.startedAt(), commissionReadResult.startedAt()),
//                getOrDefault(request.endedAt(), commissionReadResult.endedAt()),
//                commissionReadResult.writerName()
//        );
//
//        TagServiceCommand tagServiceCommand = new TagServiceCommand(
//                commissionCode,
//                getOrDefault(request.tagCode(), tagReadResult.tagCodes())
//        );
//
//        commissionsService.update(commissionsServiceCommand, commissionCode);
//        commissionsTagService.update(tagServiceCommand, commissionCode);
//
//        if (request.plannedHires() != null || request.eligibleApplicants() != null) {
//            //endContractInfo(commissionCode, request.plannedHires(), request.eligibleApplicants());
//        }
//
//        List<String> updatedKeys = request.fileKeys();
//        if (request.fileKeys() == null) {
//            DownloadFileComponentRequest downloadFileComponentRequest = new DownloadFileComponentRequest(
//                    commissionCode);
//            ResponseDto<DownloadFileComponentResponse> downloadFileComponents = fileManagementClient.getDownloadFileComponent(
//                    downloadFileComponentRequest);
//            updatedKeys = downloadFileComponents.data().urls().stream()
//                    .map(PresignedUrlComponent::key)
//                    .toList();
//        }
//
//        FilesRequestDto filesRequestDto = new FilesRequestDto(commissionCode, updatedKeys);
//        ResponseDto<Empty> updateFileComponents = fileManagementClient.updateFileStatus(filesRequestDto);
//
//        if (updateFileComponents == null) {
//            throw new ExternalServerException(CustomStatusCode.INTERNAL_MODULE_SERVER_ERROR, "응답 없음");
//        }
//
//        // kafka
//        CommissionsServiceResult commissionUpdateResult = commissionsService.read(commissionCode);
//        TagServiceResult tagResult = commissionsTagService.read(commissionUpdateResult.code());
//        CommissionServiceMessage updateMessage = new CommissionServiceMessage(
//                commissionCode,
//                commissionUpdateResult.title(),
//                commissionUpdateResult.content(),
//                commissionUpdateResult.memberCode(),
//                commissionUpdateResult.writerName(),
//                tagResult.tagCodes(),
//                commissionUpdateResult.startedAt(),
//                commissionUpdateResult.endedAt(),
//                commissionUpdateResult.paymentType(),
//                Long.parseLong(commissionUpdateResult.unitAmount()),
//                commissionUpdateResult.recruitmentStatus().equals(RecruitmentStatus.OPEN),
//                commissionUpdateResult.updatedAt()
//        );
//        commissionKafkaService.updateProducer(updateMessage);

        return new CommissionUpdateResponse(commissionCode);
    }

    @Transactional
    public void hardDeleteCommission(String memberCode, String commissionCode) {
        commissionsService.delete(memberCode, commissionCode);
        commissionsTagService.delete(memberCode, commissionCode);

        applicationEventPublisher.publishEvent(CommissionDeleteEventFactory.createEvent(commissionCode));
    }

    @Transactional
    public void deleteCommission(String code, String commissionCode) {
        // soft delete로  변환 예정
        commissionsService.delete(code, commissionCode);
        commissionsTagService.delete(code, commissionCode);

        // kafka
        applicationEventPublisher.publishEvent(CommissionDeleteEventFactory.createEvent(commissionCode));
    }

    @Transactional
    public void finishCommission(String code, String commissionCode) {
        if (!commissionsService.isOwner(code, commissionCode)) {
            throw new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION);
        }

        commissionsService.closeCommission(commissionCode);

        CommissionReadResult commissionResult = commissionsService.read(commissionCode);
        TagsReadResult tagResult = commissionsTagService.read(commissionResult.code());

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
                commissionResult.unitAmount(),
                false,
                commissionResult.updatedAt()
        );

        // kafka
        commissionKafkaService.finishProducer(finishMessage);

    }

    @Transactional
    public void openCommission(String memberCode, String commissionCode) {
        if (!commissionsService.isOwner(memberCode, commissionCode)) {
            throw new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION);
        }

        // 추후 선정 인원수를 보고 예외 처리하는 코드 추가

        commissionsService.openCommission(commissionCode);
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

        Page<CommissionReadResult> resultPage = commissionsService.getPage(code, adjustedPageable);

        List<String> commissionCodes = resultPage.stream()
                .map(CommissionReadResult::code)
                .toList();

        List<TagsReadResult> tagsReadResults = commissionsTagService.getTags(commissionCodes);

        Map<String, List<String>> tagMap = tagsReadResults.stream()
                .collect(Collectors.toMap(
                        TagsReadResult::commissionCode,
                        TagsReadResult::tagCodes
                ));

        List<CommissionReadResponse> responses = resultPage.stream()
                .map(result -> new CommissionReadResponse(
                        result.title(),
                        result.paymentType(),
                        result.unitAmount(),
                        result.startedAt(),
                        result.endedAt(),
                        result.recruitmentStatus(),
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

    @Transactional
    public CommissionRecruitmentStatusResponse getRecruitmentStatus(String commissionCode) {
        CommissionReadResult commissionsServiceResult = commissionsService.read(
                commissionCode);

        return new CommissionRecruitmentStatusResponse(
                commissionsServiceResult.recruitmentStatus().equals(RecruitmentStatus.OPEN));
    }


    private <T> T getOrDefault(T newValue, T oldValue) {
        return newValue != null ? newValue : oldValue;
    }


}
