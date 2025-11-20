package com.example.profileservice.selfPromotion.model.entity;

import com.example.profileservice.common.model.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hexagon.core.vo.PaymentType;

@Entity
@Table(name = "self_promotions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfPromotionEntity extends BaseEntity {

    // 프로필 작성 회원의 코드 FK
    @Column(name = "member_code", columnDefinition = "VARCHAR(36)", nullable = false, updatable = false)
    private String memberCode;

    // 제목
    @Column(length = 255, nullable = false)
    private String title;

    // 내용(어필)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 지급 방식: enum payment_type
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;

    // 단위 금액
    @Column(name = "unit_amount", nullable = false)
    private Long unitAmount;

    // 이력서 code FK
    @Column(name = "resume_code", columnDefinition = "VARCHAR(36)")
    private String resumeCode;

    @Builder
    public SelfPromotionEntity(String memberCode, String title, String content, PaymentType paymentType, Long unitAmount, String resumeCode) {
        this.memberCode = memberCode;
        this.title = title;
        this.content = content;
        this.paymentType = paymentType;
        this.unitAmount = unitAmount;
        this.resumeCode = resumeCode;
    }

    // 정적 팩토리 메서드로 생성 로직 캡슐화
    public static SelfPromotionEntity create(String memberCode, String title, String content, PaymentType paymentType, Long unitAmount, String resumeCode) {
        return SelfPromotionEntity.builder()
                .memberCode(memberCode)
                .title(title)
                .content(content)
                .paymentType(paymentType)
                .unitAmount(unitAmount)
                .resumeCode(resumeCode)
                .build();
    }

    public void update(String title, String content, PaymentType paymentType, Long unitAmount, String resumeCode) {
        this.title = title;
        this.content = content;
        this.paymentType = paymentType;
        this.unitAmount = unitAmount;
        this.resumeCode = resumeCode;
    }

    // 권한 검사 메서드 (BaseEntity.code를 사용)
    public boolean isOwner(String memberCode) {
        return this.memberCode.equals(memberCode);
    }
}
