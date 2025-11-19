package com.example.contractservice.deposit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "deposits")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_code", nullable = false, columnDefinition = "CHAR(36)")
    private String memberCode;

    @Column(name = "code", nullable = false, columnDefinition = "CHAR(36)")
    private String code;

    @Column(name = "amount", nullable = false)
    private Long amount;

    private DepositEntity(String code, String memberCode, Long amount) {
        this.code = (code == null) ? generateCode() : code;
        this.memberCode = memberCode;
        this.amount = (amount == null) ? 0L : amount;
    }

    public static DepositEntity createBy(String memberCode) {
        return new DepositEntity(null, memberCode, 0L);
    }

    public void updateInfo(Long amount) {
        this.amount = amount;
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }
}
