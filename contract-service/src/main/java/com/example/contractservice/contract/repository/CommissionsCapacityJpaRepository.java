package com.example.contractservice.contract.repository;

import com.example.contractservice.contract.entity.CommissionsCapacity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommissionsCapacityJpaRepository extends JpaRepository<CommissionsCapacity, Long> {
    Optional<CommissionsCapacity> findByCommissionCode(String commissionCode);

    @Modifying
    @Query(value = """
        UPDATE commissions_capacity
        SET applied_count = applied_count + 1
        WHERE commission_code = :commissionCode AND apply_capacity > applied_count
    """, nativeQuery = true)
    int increaseAppliedCount(String commissionCode);
}
