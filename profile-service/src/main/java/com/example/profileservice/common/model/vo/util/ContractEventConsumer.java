package com.example.profileservice.common.model.vo.util;

import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.rating.model.dto.request.RatingRequest;
import com.example.profileservice.rating.service.RatingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class ContractEventConsumer {

    // 주입받을 서비스 (실제 평가 로직 호출 시 사용)
    private final RatingService ratingService;
    private final ObjectMapper objectMapper;

    private static final String CONTRACT_TOPIC = "${kafka.topic.contract.name}";
    private static final String CONTRACT_STATUS_DONE = "DONE";
    private static final RatingRequest DUMMY_SATISFIED_REQUEST = new RatingRequest(true);

    @KafkaListener(topics = CONTRACT_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeContractEvent(String message) {
        ContractEvent event;
        try {
            event = objectMapper.readValue(message, ContractEvent.class);
            log.info("Contract Event received: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Contract Event JSON: {}", message, e);
            return;
        }

        if (CONTRACT_STATUS_DONE.equalsIgnoreCase(event.status())) {
            log.info("Processing DONE contract event for code: {}", event.contractCode());

            String clientCode = event.memberCode();
            String freelancerCode = event.contractCode();

            // 1. 클라이언트가 프리랜서에게 평가 (Client -> Freelancer)
            try {
                // 클라이언트(Caller)가 프리랜서(Receiver)를 평가
                log.info("Attempting to trigger Client({}) -> Freelancer({}) rating.", clientCode, freelancerCode);
                ratingService.updateRating(clientCode, freelancerCode, DUMMY_SATISFIED_REQUEST);
                log.info("Client -> Freelancer rating successful.");
            } catch (CustomException e) { // 비즈니스 예외만 catch
                log.error("Failed (Business Error) Client -> Freelancer rating for contract {}. Error: {}", event.contractCode(), e.getMessage());
                // 비즈니스 예외는 재시도 필요 없으므로 return하지 않고 다음 로직으로 진행
            } catch (Exception e) { // 그 외 시스템 오류는 throw (재시도 유발)
                log.error("Failed (System Error) Client -> Freelancer rating for contract {}. Error: {}", event.contractCode(),
                        e.getMessage());

                throw new RuntimeException("System error during rating update.", e);
            }

            // 2. 프리랜서가 클라이언트에게 평가 (Freelancer -> Client)
            try {
                // 프리랜서(Caller)가 클라이언트(Receiver)를 평가
                log.info("Attempting to trigger Freelancer({}) -> Client({}) rating.", freelancerCode, clientCode);
                ratingService.updateRating(freelancerCode, clientCode, DUMMY_SATISFIED_REQUEST);
                log.info("Freelancer -> Client rating successful.");
            } catch (CustomException e) { // 🟢 비즈니스 예외만 catch
                log.error("Failed (Business Error) Freelancer -> Client rating for contract {}. Error: {}", event.contractCode(), e.getMessage());
            } catch (Exception e) { // 🟢 그 외 시스템 오류는 throw (재시도 유발)
                log.error("Failed (System Error) Freelancer -> Client rating for contract {}. Error: {}", event.contractCode(), e.getMessage());

                throw new RuntimeException("System error during rating update.", e); // 👈 재시도를 위해 throw
            }

        } else {
            log.debug("Skipping contract event with status: {}", event.status());
        }
    }
}
