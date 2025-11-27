package com.example.cartpostservice.commissions.model;

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
import org.hexagon.core.vo.PaymentType;

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
    private String unitAmount;

    @Column(nullable = false)
    private LocalDate startedAt;

    @Column(nullable = false)
    private LocalDate endedAt;

    @Column(nullable = false)
    private boolean isOpen = true;

    @Column(nullable = false)
    private String writerName;

    @Builder
    public CommissionsEntity(String memberCode, String title, String content, PaymentType paymentType,
            String unitAmount, LocalDate startedAt, LocalDate endedAt, String writerName) {
        this.memberCode = memberCode;
        this.title = title;
        this.content = content;
        this.paymentType = paymentType;
        this.unitAmount = unitAmount;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.writerName = writerName;
    }

    public void closed() {
        this.isOpen = false;
    }
}
