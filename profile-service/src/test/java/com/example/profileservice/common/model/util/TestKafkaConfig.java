package com.example.profileservice.common.model.util;


import com.example.profileservice.common.model.vo.KafkaProducer;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestKafkaConfig {

    @Bean
    @Primary // 기존의 KafkaProducer Bean을 덮어씁니다.
    public KafkaProducer mockKafkaProducer() {
        KafkaProducer mockProducer = Mockito.mock(KafkaProducer.class);
        // send 메서드가 호출될 때 아무 작업도 하지 않도록 설정
        Mockito.doNothing().when(mockProducer).send(Mockito.anyString(), Mockito.any());
        return mockProducer;
    }
}
