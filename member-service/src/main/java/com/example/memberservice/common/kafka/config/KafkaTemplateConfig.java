package com.example.memberservice.common.kafka.config;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaTemplateConfig {

    @Value("${kafka.topic.member.create-topic}")
    private String memberCreateTopicName;

    @Value("${kafka.topic.member.update-topic}")
    private String memberUpdateTopicName;

    @Value("${kafka.topic.member.delete-topic}")
    private String memberDeleteTopicName;

    @Value("${kafka.topic.member.delete-freelancer-role-topic}")
    private String memberDeleteFreelancerRoleTopicName;

    @Value("${kafka.topic.member.delete-client-role-topic}")
    private String memberDeleteClientRoleTopicName;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Value("${kafka.config.topic-partitions}")
    private int topicPartitions;

    @Value("${kafka.config.topic-replications}")
    private int topicReplications;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
        ProducerFactory<String, Object> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic memberCreateTopic() {
        return TopicBuilder.name(memberCreateTopicName)
            .partitions(topicPartitions)
            .replicas(topicReplications)
            .build();
    }

    @Bean
    public NewTopic memberUpdateTopic() {
        return TopicBuilder.name(memberUpdateTopicName)
            .partitions(topicPartitions)
            .replicas(topicReplications)
            .build();
    }


    @Bean
    public NewTopic memberDeleteTopic() {
        return TopicBuilder.name(memberDeleteTopicName)
            .partitions(topicPartitions)
            .replicas(topicReplications)
            .build();
    }

    @Bean
    public NewTopic memberDeleteFreelancerRoleTopic() {
        return TopicBuilder.name(memberDeleteFreelancerRoleTopicName)
            .partitions(topicPartitions)
            .replicas(topicReplications)
            .build();
    }

    @Bean
    public NewTopic memberDeleteClientRoleTopic() {
        return TopicBuilder.name(memberDeleteClientRoleTopicName)
            .partitions(topicPartitions)
            .replicas(topicReplications)
            .build();
    }
}
