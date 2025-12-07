package com.example.cartpostservice.commissions.service.kafka;

import com.example.cartpostservice.commissions.controller.dto.request.CommissionCreateRequest;
import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.dto.response.TagServiceResult;
import com.example.cartpostservice.commissions.service.kafka.dto.request.CommissionCreateMessage;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.commission.CommissionCreatedEvent;
import org.hexagon.core.events.commission.CommissionDeletedEvent;
import org.hexagon.core.events.commission.CommissionUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommissionKafkaService {

    private final CommissionsRepository commissionsRepository;

    // KafkaTemplate 주입 (Config에서 빈으로 등록한 타입과 일치해야 함)
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // 전송할 토픽 이름 주입 (application.yml/properties 값)
    @Value("${search.topic.name}")
    private String searchTopicName;

    @Transactional
    public void createProducer(CommissionCreateMessage createMessage){

        CommissionCreatedEvent commissionCreatedEvent = new CommissionCreatedEvent(
                createMessage.code(),
                createMessage.title(),
                createMessage.content(),
                createMessage.memberCode(),
                createMessage.memberNickname(),
                createMessage.tags(),
                createMessage.startedAt(),
                createMessage.endedAt(),
                createMessage.paymentType(),
                createMessage.payAmount(),
                createMessage.isClosed(),
                createMessage.updatedAt()
        );

        kafkaTemplate.send(searchTopicName, commissionCreatedEvent);
    }

    public void updateProducer(String commissionCode, CommissionCreateRequest request) {

        CommissionsEntity commissionsEntity = commissionsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_COMMISSION));

        CommissionUpdatedEvent commissionUpdatedEvent = new CommissionUpdatedEvent(
                commissionCode,
                commissionsEntity.getTitle(),
                commissionsEntity.getContent(),
                commissionsEntity.getMemberCode(),
                commissionsEntity.getWriterName(),
                request.tagCode(),
                request.startedAt(),
                request.endedAt(),
                commissionsEntity.getPaymentType(),
                Long.parseLong(commissionsEntity.getUnitAmount()),
                commissionsEntity.isOpen(),
                commissionsEntity.getUpdatedAt()
        );

        kafkaTemplate.send(searchTopicName, commissionUpdatedEvent);
    }

    public void deleteProducer(String commissionCode) {
        CommissionDeletedEvent commissionDeletedEvent = new CommissionDeletedEvent(commissionCode);

        kafkaTemplate.send(searchTopicName, commissionDeletedEvent);
    }

    public void finishProducer(String commissionCode, TagServiceResult tagResult) {

        CommissionsEntity commissionsEntity = commissionsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_COMMISSION));

        CommissionUpdatedEvent commissionUpdatedEvent = new CommissionUpdatedEvent(
                commissionCode,
                commissionsEntity.getTitle(),
                commissionsEntity.getContent(),
                commissionsEntity.getMemberCode(),
                commissionsEntity.getWriterName(),
                tagResult.tagCodes(),
                commissionsEntity.getStartedAt(),
                commissionsEntity.getEndedAt(),
                commissionsEntity.getPaymentType(),
                Long.parseLong(commissionsEntity.getUnitAmount()),
                commissionsEntity.isOpen(),
                commissionsEntity.getUpdatedAt()
        );

        kafkaTemplate.send(searchTopicName, commissionUpdatedEvent);
    }
}
