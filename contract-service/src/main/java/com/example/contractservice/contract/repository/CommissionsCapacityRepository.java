package com.example.contractservice.contract.repository;

import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static  com.example.contractservice.contract.domain.exception.ContractErrorCode.COMMISSION_CAPACITY_NOT_FOUND;

@Repository
@RequiredArgsConstructor
public class CommissionsCapacityRepository {
    private final CommissionsCapacityJpaRepository commissionsCapacityJpaRepository;

    public CommissionsCapacity findByCommissionCode(String commissionCode) {
        return commissionsCapacityJpaRepository.findByCommissionCode(commissionCode)
                .orElseThrow(() -> new ContractException(COMMISSION_CAPACITY_NOT_FOUND));
    }

    public void saveCapacity(CommissionsCapacity capacity) {
        commissionsCapacityJpaRepository.save(capacity);
    }
}
