package com.example.cartpostservice.cart.model;

import com.example.cartpostservice.cart.model.vo.ContractStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.*;
import org.hexagon.core.vo.PaymentType;

@Entity
@Table(name = "cart_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CartItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "contract_code", nullable = false, updatable = false)
    private String contractCode;

    @Column(name = "cart_code", nullable = false, updatable = false)
    private String cartCode;

    @Column(name = "commission_code", nullable = false, updatable = false)
    private String commissionCode;

    @Column(name = "client_code", nullable = false, updatable = false)
    private String clientCode;

    @Column(name = "freelancer_code", nullable = false, updatable = false)
    private String freelancerCode;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at", nullable = false)
    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column
    private String clientName;

    @Column
    private String freelancerName;

    @Column
    private String contractTitle;

    @PrePersist
    public void prePersist() {
        if (code == null) {
            code = UUID.randomUUID().toString();
        }
    }
}

