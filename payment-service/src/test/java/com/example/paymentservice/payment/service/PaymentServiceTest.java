package com.example.paymentservice.payment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentService paymentService; // 인터페이스를 Mock 처리

    @Test
    @DisplayName("payForRecharge 성공 테스트")
    void testPayForRecharge() {

    }

    @Test
    @DisplayName("getAllPayments 성공 테스트")
    void testGetAllPayments() {

    }

    @Test
    @DisplayName("getPaymentByOrderCode 성공 테스트")
    void testGetPaymentByOrderCode() {

    }
}

