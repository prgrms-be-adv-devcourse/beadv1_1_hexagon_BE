package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.mapper.CommissionMapper;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheCreatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionCacheUpdatedCommand;
import com.example.cartpostservice.commissions.service.usecase.command.CommissionsServiceCommand;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionIndexReadResult;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionReadResult;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionsService implements CrudService<CommissionsServiceCommand, CommissionReadResult, String> {

    private final CommissionsRepository commissionsRepository;
    private final CommissionMapper commissionMapper;

    @Override
    public String create(CommissionsServiceCommand createCommand) {
        CommissionsEntity createdCommission = commissionMapper.toEntity(createCommand);

        CommissionsEntity savedCommission = commissionsRepository.save(createdCommission);

        return savedCommission.getCode();
    }

    @Override
    public CommissionReadResult read(String commissionsCode) {

        CommissionsEntity commission = commissionsRepository.findByCode(commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_COMMISSION));

        return commissionMapper.toDto(commission);
    }

    @Override
    public void update(CommissionsServiceCommand updatedCommand, String commissionCode) {
        List<CommissionsEntity> commissions = commissionsRepository.findByMemberCode(
                updatedCommand.memberCode());
        if (commissions.isEmpty()) {
            throw new BusinessException(CustomStatusCode.BAD_REQUEST_COMMISSION);
        }

        CommissionsEntity foundEntity = commissions.stream()
                .filter(entity -> entity.getCode().equals(commissionCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commissionMapper.updateCommission(updatedCommand, foundEntity);

        // 자동 반영
        //CommissionsEntity saved = commissionsRepository.save(foundEntity);
    }

    @Override
    public void delete(String memberCode, String commissionsCode) {
        CommissionsEntity commission = commissionsRepository.findByMemberCodeAndCode(memberCode, commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commissionsRepository.delete(commission);
    }

    public void softDelete(String memberCode, String commissionsCode) {
        CommissionsEntity commission = commissionsRepository.findByMemberCodeAndCode(memberCode, commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commission.delete();
    }

    public Page<CommissionIndexReadResult> getPage(String memberCode, Pageable pageable) {

        Page<CommissionsEntity> commissions = commissionsRepository.findPageByMemberCode(memberCode, pageable);

        return commissions.map(commissionMapper::toIndexDto);
    }


    public void createCacheInfo(CommissionCacheCreatedCommand cacheCommand) {
        CommissionsEntity commission = commissionsRepository.findByCode(cacheCommand.commissionCode())
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commission.updatePersonInfo(cacheCommand.eligibleApplicants(), 0, cacheCommand.plannedHires(), 0);
        commission.updateLastSyncTime(Instant.now());
    }

    public void updateCacheInfo(CommissionCacheUpdatedCommand cacheCommand) {
        CommissionsEntity commission = commissionsRepository.findByCode(cacheCommand.commissionCode())
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commission.updatePersonInfo(cacheCommand.eligibleApplicants(), cacheCommand.appliedCount(),
                cacheCommand.plannedHires(), cacheCommand.selectedCount());
        commission.updateLastSyncTime(Instant.now());
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
            throw new BusinessException(CustomStatusCode.CANNOT_OPEN_COMMISSION);
        }

        commission.openRecruitmentStatus();
    }


}