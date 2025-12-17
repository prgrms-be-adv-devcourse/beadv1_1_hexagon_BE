package com.example.cartpostservice.commissions.model;

import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import java.time.Instant;
import org.hexagon.core.vo.PaymentType;
import com.example.cartpostservice.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "commissions")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommissionsEntity extends BaseEntity {

    @Column(nullable = false)
    private String memberCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private Long unitAmount;

    @Column(nullable = false)
    private LocalDate startedAt;

    @Column(nullable = false)
    private LocalDate endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitmentStatus recruitmentStatus;

    @Column(nullable = false)
    private String writerName;

    @Column(nullable = true)
    private int cacheApplyCapacity;

    @Column(nullable = true)
    private int cacheAppliedCount;

    @Column(nullable = true)
    private int cacheSelectionCapacity;

    @Column(nullable = true)
    private int cacheSelectedCount;

    @Column(nullable = true)
    private Instant lastSyncTime;


    @Builder
    public CommissionsEntity(String memberCode, String title, String content, PaymentType paymentType,
            Long unitAmount, LocalDate startedAt, LocalDate endedAt, RecruitmentStatus recruitmentStatus,
            String writerName) {
        this.memberCode = memberCode;
        this.title = title;
        this.content = content;
        this.paymentType = paymentType;
        this.unitAmount = unitAmount;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.recruitmentStatus = recruitmentStatus;
        this.writerName = writerName;
    }

    public void update(String memberCode, String title, String content, PaymentType paymentType,
            Long unitAmount, LocalDate startedAt, LocalDate endedAt, String writerName) {
        this.memberCode = memberCode;
        this.title = title;
        this.content = content;
        this.paymentType = paymentType;
        this.unitAmount = unitAmount;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.writerName = writerName;
    }

    public void updatePersonInfo(int cacheApplyCapacity, int cacheAppliedCount, int cacheSelectionCapacity,
            int cacheSelectedCount) {
        this.cacheApplyCapacity = cacheApplyCapacity;
        this.cacheAppliedCount = cacheAppliedCount;
        this.cacheSelectionCapacity = cacheSelectionCapacity;
        this.cacheSelectedCount = cacheSelectedCount;
        lastSyncTime = Instant.now();
    }

    public void updateLastSyncTime(Instant lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public void closeRecruitmentStatus() {
        this.recruitmentStatus = RecruitmentStatus.CLOSED;
    }

    public void haltRecruitmentStatus() {
        this.recruitmentStatus = RecruitmentStatus.HALTED;
    }

    public void openRecruitmentStatus() {
        this.recruitmentStatus = RecruitmentStatus.OPEN;
    }
}
