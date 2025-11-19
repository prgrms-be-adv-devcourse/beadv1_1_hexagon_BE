package com.example.contractservice.deposit.repository;

import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.NO_DEPOSIT_ENTITY;

import com.example.contractservice.deposit.domain.exception.DepositException;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.entity.DepositHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DepositRepository {
    private final DepositJpaRepository depositJpaRepository;
    private final DepositHistoryJpaRepository depositHistoryJpaRepository;

    public DepositEntity findDepositByMemberCode(String memberCode) {
        return depositJpaRepository.findByMemberCode(memberCode)
                .orElseThrow(() -> new DepositException(NO_DEPOSIT_ENTITY));
    }

    public DepositEntity saveDeposit(DepositEntity depositEntity) {
        return depositJpaRepository.save(depositEntity);
    }

    public DepositHistoryEntity saveDepositHistory(DepositHistoryEntity depositHistoryEntity) {
        return depositHistoryJpaRepository.save(depositHistoryEntity);
    }

    public boolean existMemberDeposit(String memberCode) {
        return depositJpaRepository.existsByMemberCode(memberCode);
    }
}
