package com.example.contractservice.settlement.repository;

import com.example.contractservice.settlement.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SettlementJpaRepository extends JpaRepository<SettlementEntity, Long> {

    @Modifying
    @Query("""
        DELETE
        FROM SettlementEntity s
        WHERE s.contractCode = :contractCode
    """)
    void deleteByContractCode(String contractCode);
}
