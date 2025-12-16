package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheCreatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionsServiceCommand;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import com.example.cartpostservice.commissions.service.mapper.CommissionMapper;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionsService implements CrudService<CommissionsServiceCommand, CommissionReadResult, String> {

    private final CommissionsRepository commissionsRepository;

    @Override
    public String create(CommissionsServiceCommand createCommand) {
        CommissionsEntity createdCommission = CommissionMapper.toEntity(createCommand);

        CommissionsEntity savedCommission = commissionsRepository.save(createdCommission);

        return savedCommission.getCode();
    }

    @Override
    public CommissionReadResult read(String commissionsCode) {

        CommissionsEntity commission = commissionsRepository.findByCode(commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_COMMISSION));

        return CommissionMapper.toDto(commission);
    }

    @Override
    public void update(CommissionsServiceCommand commissionsServiceCommand, String commissionsCode) {
        List<CommissionsEntity> commissions = commissionsRepository.findByMemberCode(
                commissionsServiceCommand.memberCode());
        if (commissions.isEmpty()) {
            throw new BusinessException(CustomStatusCode.BAD_REQUEST_COMMISSION);
        }

        CommissionsEntity foundEntity = commissions.stream()
                .filter(entity -> entity.getCode().equals(commissionsCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        foundEntity.update(
                commissionsServiceCommand.memberCode(),
                commissionsServiceCommand.title(),
                commissionsServiceCommand.content(),
                commissionsServiceCommand.paymentType(),
                commissionsServiceCommand.unitAmount(),
                commissionsServiceCommand.startedAt(),
                commissionsServiceCommand.endedAt(),
                commissionsServiceCommand.writerName()
        );

        // 자동 반영
        //CommissionsEntity saved = commissionsRepository.save(foundEntity);
    }

    @Override
    public void delete(String memberCode, String commissionsCode) {
        CommissionsEntity commission = commissionsRepository.findByMemberCodeAndCode(memberCode, commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commissionsRepository.delete(commission);
    }

    public Page<CommissionReadResult> getPage(String memberCode, Pageable pageable) {

        Page<CommissionsEntity> commissions = commissionsRepository.findPageByMemberCode(memberCode, pageable);

        return null;
//        return commissions.map(commission ->
//                new CommissionsServiceResult(
//                        commission.getCode(),
//                        commission.getMemberCode(),
//                        commission.getTitle(),
//                        commission.getContent(),
//                        commission.getPaymentType(),
//                        commission.getUnitAmount(),
//                        commission.getStartedAt(),
//                        commission.getEndedAt(),
//                        commission.getRecruitmentStatus(),
//                        commission.getWriterName(),
//                        commission.getUpdatedAt()
//                )
//        );
    }


    public void updateCacheInfo(CommissionCacheCreatedCommand cacheCommand) {
        CommissionsEntity commission = commissionsRepository.findByCode(cacheCommand.commissionCode())
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commission.updatePersonInfo(cacheCommand.eligibleApplicants(), 0, cacheCommand.eligibleApplicants(), 0);
    }

    public boolean isOwner(String memberCode, String commissionsCode) {
        return commissionsRepository
                .findByMemberCodeAndCode(memberCode, commissionsCode)
                .isPresent();
    }

    public void closeCommission(String commissionCode) {
        CommissionsEntity commission = commissionsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        if (commission.getRecruitmentStatus() != RecruitmentStatus.OPEN) {
            throw new BusinessException(CustomStatusCode.ALREADY_CLOSED_COMMISSION);
        }

        commission.closeRecruitmentStatus();
    }

    public void openCommission(String commissionCode) {
        CommissionsEntity commission = commissionsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        if (commission.getRecruitmentStatus() == RecruitmentStatus.HALTED) {
            throw new BusinessException(CustomStatusCode.NOT_OPEN_COMMISSION);
        }

        commission.openRecruitmentStatus();
    }


}