package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.event.CommissionUpsertEventFactory;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.TagsReadResult;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommissionStatusEventToCommissionService {

    private final CommissionsRepository commissionsRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CommissionsService commissionsService;
    private final CommissionsTagService commissionsTagService;

    @Transactional
    public void changeCommissionStatus(String commissionCode, boolean isOpen) {
        CommissionsEntity commission = commissionsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_COMMISSION));

        if (isOpen) {
            commission.openRecruitmentStatus();
            return;
        }

        commission.closeRecruitmentStatus();

        CommissionReadResult commissionResult = commissionsService.read(commissionCode);
        TagsReadResult tagResult = commissionsTagService.read(commissionResult.code());

        applicationEventPublisher.publishEvent(CommissionUpsertEventFactory.createEvent(commissionResult, tagResult));
    }

}
