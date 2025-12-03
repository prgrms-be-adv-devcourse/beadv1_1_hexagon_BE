package com.example.paymentservice.payment.service;

import com.example.paymentservice.payment.controller.dto.request.DepositRechargeRequest;
import com.example.paymentservice.payment.controller.dto.response.DepositRechargeResponse;
import com.example.paymentservice.payment.controller.internal.ContractClient;
import com.example.paymentservice.payment.model.PaymentEntity;
import com.example.paymentservice.payment.model.PaymentStatus;
import com.example.paymentservice.payment.repository.OrderRepository;
import com.example.paymentservice.payment.repository.PaymentRepository;
import com.example.paymentservice.payment.service.dto.response.PaymentConfirmResponse;
import com.example.paymentservice.payment.service.dto.response.PaymentGetResult;
import com.example.paymentservice.payment.service.dto.response.PaymentsGetResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper om = new ObjectMapper();
    private final ContractClient contractClient;

    @Transactional
    public void confirmAndSave(InputStream tossResponseStream, String memberCode) throws Exception {
        InputStreamReader reader = new InputStreamReader(tossResponseStream, StandardCharsets.UTF_8);
        PaymentConfirmResponse paymentResponse = om.readValue(reader, PaymentConfirmResponse.class);
        log.info("Payment Confirm Response: {}", paymentResponse);

        PaymentEntity payment = PaymentEntity.builder()
                .paymentKey(paymentResponse.paymentKey())
                .orderPgId(paymentResponse.orderId())
                .amount((long)paymentResponse.amount())
                .paymentStatus(PaymentStatus.valueOf(paymentResponse.status()))
                .method(paymentResponse.method())
                .approveAt(OffsetDateTime.parse(paymentResponse.approvedAt()).toInstant())
                .build();

        paymentRepository.save(payment);
        payForRecharge(memberCode, (long)paymentResponse.amount());
    }

    @Override
    public void payForRecharge(String memberCode, Long amount) {

        DepositRechargeRequest request = new DepositRechargeRequest(
                memberCode,
                amount
        );
        // 2. Feign Client 호출
        ResponseDto<DepositRechargeResponse> responseDto = contractClient.recharge(request);
    }

    @Override
    public PaymentsGetResult getAllPayments(String code) {
        return null;
    }

    @Override
    public PaymentGetResult getPaymentByOrderCode(String code, String orderPgId) {
        return null;
    }
}
