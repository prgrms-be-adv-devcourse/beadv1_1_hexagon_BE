package com.example.cartpostservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfiguration {

    @Value("${cartpost.config.topic-partitions}")
    private int topic_partitions;

    @Value("${cartpost.config.topic-replications}")
    private int topic_replications;

    @Value("${kafka.topic.contract.name}")
    private String contractTopicName;

    @Value("${kafka.topic.commission.name}")
    private String commissionEventTopicName;

    @Value("${kafka.topic.commission.status.name}")
    private String commissionStatusTopicName;

    @Value("${kafka.topic.member.create.name}")
    private String memberCreateTopicName;

    @Value("${kafka.topic.member.role.name}")
    private String memberRoleTopicName;

    @Value("${kafka.topic.member.cancel.name}")
    private String memberCancelTopicName;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic createContractTopic() {
        return TopicBuilder.name(contractTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createCommissionEVentTopic() {
        return TopicBuilder.name(commissionEventTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createCommissionStatusTopic() {
        return TopicBuilder.name(commissionStatusTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createMemberCreateTopic() {
        return TopicBuilder.name(memberCreateTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createMemberRoleTopic() {
        return TopicBuilder.name(memberRoleTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }

    @Bean
    public NewTopic createMemberCancelTopic() {
        return TopicBuilder.name(memberCancelTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }


}
