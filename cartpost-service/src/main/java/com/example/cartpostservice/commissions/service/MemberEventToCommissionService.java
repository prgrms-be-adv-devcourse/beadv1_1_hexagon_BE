package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.event.CommissionDeleteEventFactory;
import com.example.cartpostservice.commissions.service.event.CommissionUpsertEventFactory;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.TagsReadResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberEventToCommissionService {

    private final CommissionsRepository commissionsRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CommissionsService commissionsService;
    private final CommissionsTagService commissionsTagService;

    @Transactional
    public void stopCommissions(String memberCode) {
        List<CommissionsEntity> commissions = commissionsRepository.findByMemberCode(memberCode);

        commissions.forEach(CommissionsEntity::haltRecruitmentStatus);

        for (CommissionsEntity commissionsEntity : commissions) {
            CommissionReadResult commissionResult = commissionsService.read(commissionsEntity.getCode());
            TagsReadResult tagResult = commissionsTagService.read(commissionResult.code());

            applicationEventPublisher.publishEvent(
                    CommissionUpsertEventFactory.createEvent(commissionResult, tagResult));
        }
    }

    @Transactional
    public void deleteCommissions(String memberCode) {
        List<CommissionsEntity> commissions = commissionsRepository.findByMemberCode(memberCode);

        commissions.forEach((commissionsEntity) -> {
            boolean b = commissionsEntity.getRecruitmentStatus().equals(RecruitmentStatus.OPEN);
            if (b) {
                commissionsEntity.haltRecruitmentStatus();
            }
            commissionsEntity.delete();
        });

        for (CommissionsEntity commissionsEntity : commissions) {
            applicationEventPublisher.publishEvent(
                    CommissionDeleteEventFactory.createEvent(commissionsEntity.getCode()));
        }


    }
}
