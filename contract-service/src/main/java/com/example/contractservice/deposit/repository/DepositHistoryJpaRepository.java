package com.example.contractservice.deposit.repository;

import com.example.contractservice.deposit.entity.DepositHistoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepositHistoryJpaRepository extends JpaRepository<DepositHistoryEntity, Long> {

    @Query("""
        SELECT dh
        FROM DepositHistoryEntity dh
        WHERE dh.depositCode = :depositCode AND dh.contractCode = :contractCode
    """)
    Optional<DepositHistoryEntity> findByDepositAndContract(String depositCode, String contractCode);
}
