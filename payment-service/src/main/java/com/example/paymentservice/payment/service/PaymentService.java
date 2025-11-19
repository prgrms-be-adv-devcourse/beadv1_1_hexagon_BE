package com.example.paymentservice.payment.service;

import com.example.paymentservice.payment.service.dto.response.PayRechargeResult;
import com.example.paymentservice.payment.service.dto.response.PaymentGetResult;
import com.example.paymentservice.payment.service.dto.response.PaymentsGetResult;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

    public void payForRecharge(String memberCode, Long amount);

    public PaymentsGetResult getAllPayments(String code);

    public PaymentGetResult getPaymentByOrderCode(String code, String orderPgId);
}
