package com.example.contractservice.contract.repository;

import com.example.contractservice.contract.entity.ContractEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContractJpaRepository extends JpaRepository<ContractEntity, Long> {

    Optional<ContractEntity> findByCode(String code);

    @Query("""
        SELECT c
        FROM ContractEntity c
        WHERE c.code IN :codes
    """)
    List<ContractEntity> findAllByCodes(List<String> codes);

    @Query(value = """
        SELECT freelancer_code
        FROM contracts
        WHERE status = :status AND commission_code = :commissionCode
    """, nativeQuery = true)
    List<String> findFreelancerBy(String commissionCode, String status);
}
