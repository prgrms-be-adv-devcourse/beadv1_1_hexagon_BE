package com.example.contractservice.settlement.entity;

import com.example.contractservice.settlement.common.SettlementStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "settlements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, columnDefinition = "CHAR(36)")
    private String code;

    @Column(name = "receiver_code", nullable = false, columnDefinition = "CHAR(36)")
    private String receiverCode;

    @Column(name = "contract_code", nullable = false, columnDefinition = "CHAR(36)")
    private String contractCode;

    @Column(name = "original_amount", nullable = false)
    private Long originalAmount;

    @Column(name = "settled_amount")
    private Long settledAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SettlementStatus status;

    @Column(name = "progressing_at", nullable = false)
    private Instant progressingAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "settled_at")
    private Instant settledAt;

    @Column(name = "settlement_rate", precision = 5, scale = 2)
    private BigDecimal settlementRate;

    @Builder
    public SettlementEntity(Long id, String code, String receiverCode, String contractCode, Long originalAmount,
            SettlementStatus status, Instant progressingAt, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.receiverCode = receiverCode;
        this.contractCode = contractCode;
        this.originalAmount = originalAmount;
        this.status = status;
        this.progressingAt = progressingAt;
        this.createdAt = createdAt;
    }

    public void updateInfo(Long settledAmount, BigDecimal settlementRate, Instant settledAt, SettlementStatus status) {
        this.settledAmount = settledAmount;
        this.settlementRate = settlementRate;
        this.settledAt = settledAt;
        this.status = status;
    }
}
