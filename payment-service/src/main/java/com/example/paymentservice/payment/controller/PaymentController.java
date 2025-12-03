package com.example.paymentservice.payment.controller;

import com.example.paymentservice.payment.controller.dto.request.PaymentConfirmRequest;
import com.example.paymentservice.payment.controller.dto.response.PayRechargeResponse;
import com.example.paymentservice.payment.controller.dto.response.PaymentGetResponse;
import com.example.paymentservice.payment.controller.dto.response.PaymentsGetResponse;
import com.example.paymentservice.payment.service.PaymentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestBody;
// @RestController로 변경하여 JSON 응답을 쉽게 처리합니다.

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController implements PaymentApi {

    private final ObjectMapper om = new ObjectMapper();
    private final PaymentServiceImpl paymentService;

    @Value("${payment.toss.widget-secret-key}")
    private String widgetSecretKey;

    @Value("${payment.toss.confirm-url}")
    private String tossPaymentConfirmUrl;

    @PostMapping("/confirm")
    public ResponseEntity<JSONObject> confirmPayment(@RequestHeader("X-CODE") String memberCode,
            @RequestBody PaymentConfirmRequest request) throws Exception {

        JSONParser parser = new JSONParser();

        log.info("confirm 로직을 수행합니다.");

        Map<String, Object> requestMap = Map.of(
                "paymentKey", request.paymentKey(),
                "orderId", request.orderId(),
                "amount", request.amount()
        );

        log.info("request amount : {}", request.amount());
        // Basic 인증 헤더
        String authorization = "Basic " + Base64.getEncoder()
                .encodeToString((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));

        // HttpClient 생성
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(tossPaymentConfirmUrl))
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(om.writeValueAsBytes(requestMap)))
                .build();

        HttpResponse<InputStream> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());

        int code = response.statusCode();
        InputStream responseStream = response.body();
        byte[] bodyBytes = responseStream.readAllBytes(); // 스트림 전체 읽기
        responseStream.close();

        // byte[]를 다시 InputStream으로 만들어서 사용
        paymentService.confirmAndSave(new ByteArrayInputStream(bodyBytes), memberCode);

        Reader reader = new InputStreamReader(new ByteArrayInputStream(bodyBytes), StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        return ResponseEntity.status(code).body(jsonObject);
    }


    @Override
    @PostMapping("/charge")
    public ResponseEntity<ResponseDto<PayRechargeResponse>> payForRecharge(@RequestHeader(name = "X-CODE") String code,
            @PathVariable String amount) {
        return null;
    }

    @Override
    @GetMapping("/orders")
    public ResponseEntity<ResponseDto<PaymentsGetResponse>> getAllPayments(
            @RequestHeader(name = "X-CODE") String code) {
        return null;
    }

    @Override
    @GetMapping("/orders/{order-pg-id}")
    public ResponseEntity<ResponseDto<PaymentGetResponse>> getPaymentByOrderCode(
            @RequestHeader(name = "X-CODE") String code, @PathVariable("order-pg-id") String orderPgId) {
        return null;
    }
}
