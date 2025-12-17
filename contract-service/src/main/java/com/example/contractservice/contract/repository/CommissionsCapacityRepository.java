package com.example.contractservice.contract.repository;

import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.COMMISSION_APPLIED_COUNT_FULL;
import static  com.example.contractservice.contract.domain.exception.ContractErrorCode.COMMISSION_CAPACITY_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class CommissionsCapacityRepository {
    private final CommissionsCapacityJpaRepository commissionsCapacityJpaRepository;

    public CommissionsCapacity findByCommissionCodeOrCreate(String commissionCode, int applyCapacity, int selectionCapacity) {
        return commissionsCapacityJpaRepository.findByCommissionCode(commissionCode)
                .orElseGet(() -> {
                    CommissionsCapacity createdCapacity = CommissionsCapacity.createBy(commissionCode, applyCapacity, selectionCapacity);

                    return commissionsCapacityJpaRepository.save(createdCapacity);
                });
    }

    public CommissionsCapacity findByCommissionCode(String commissionCode) {
        return commissionsCapacityJpaRepository.findByCommissionCode(commissionCode)
                .orElseThrow(() -> new ContractException(COMMISSION_CAPACITY_NOT_FOUND));
    }

    public void saveCapacity(CommissionsCapacity capacity) {
        commissionsCapacityJpaRepository.save(capacity);
    }

    @Transactional
    public void increaseAppliedCount(String commissionCode) {
        int updatedCount = commissionsCapacityJpaRepository.increaseAppliedCount(commissionCode);

        if (updatedCount <= 0) {
            throw new ContractException(COMMISSION_APPLIED_COUNT_FULL);
        }
    }
}
