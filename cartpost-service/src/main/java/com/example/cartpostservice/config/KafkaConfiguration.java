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

    @Value("${contract.topic.name}")
    private String contractTopicName;

    @Value("${search.topic.name}")
    private String searchTopicName;

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
    public NewTopic createSearchTopic() {
        return TopicBuilder.name(searchTopicName)
                .partitions(topic_partitions)
                .replicas(topic_replications)
                .build();
    }


}
