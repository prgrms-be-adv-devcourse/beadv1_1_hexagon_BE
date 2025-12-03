package com.example.contractservice.contract.repository;

import com.example.contractservice.contract.entity.CommissionsCapacity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionsCapacityJpaRepository extends JpaRepository<CommissionsCapacity, Long> {
    Optional<CommissionsCapacity> findByCommissionCode(String commissionCode);
}
