package com.example.cartpostservice.kafka;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.CommissionsTagEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.repository.CommissionsTagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.vo.Commission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnsentCommissionRunner implements ApplicationRunner {

    private final CommissionsRepository commissionsRepository;
    private final CommissionsTagRepository commissionsTagRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${search.topic.name}")
    private String searchTopicName;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<CommissionsEntity> commissionsToSend = commissionsRepository.findAll().stream()
                .filter(commission -> commission.getRecruitmentStatus() != RecruitmentStatus.HALTED)
                .toList();

        if (commissionsToSend.isEmpty()) {
            log.info("전송할 대기 항목이 없습니다.");
            return;
        }

        List<Commission> commissions = new ArrayList<>();
        for (CommissionsEntity commissionsEntity : commissionsToSend) {
            List<CommissionsTagEntity> tagEntities = commissionsTagRepository.findByCommissionCode(
                    commissionsEntity.getCode());
            List<String> tags = tagEntities.stream().map(CommissionsTagEntity::getTagCode).collect(Collectors.toList());

            Commission commission = new Commission(
                    commissionsEntity.getCode(),
                    commissionsEntity.getTitle(),
                    commissionsEntity.getContent(),
                    commissionsEntity.getMemberCode(),
                    commissionsEntity.getWriterName(),
                    tags,
                    commissionsEntity.getStartedAt(),
                    commissionsEntity.getEndedAt(),
                    commissionsEntity.getPaymentType(),
                    Long.valueOf(commissionsEntity.getUnitAmount()),
                    commissionsEntity.getRecruitmentStatus().equals(RecruitmentStatus.OPEN),
                    commissionsEntity.getUpdatedAt()
            );
            commissions.add(commission);
        }

        kafkaTemplate.send(searchTopicName, commissions);
    }
}




