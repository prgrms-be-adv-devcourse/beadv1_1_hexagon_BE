package com.example.profileservice;

import com.example.profileservice.common.model.vo.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ProfileServiceApplicationTests {

	@Test
	void contextLoads() {
	}

    @Configuration
    // TestKafkaConfig 대신 내부 클래스로 정의하고 @Configuration을 사용해도 @SpringBootTest가 로드합니다.
    static class TestKafkaConfig {

        @Bean
        @Primary // 기존의 KafkaProducer Bean을 덮어씁니다.
        public KafkaProducer mockKafkaProducer() {
            KafkaProducer mockProducer = Mockito.mock(KafkaProducer.class);

            // 키 없는 send(topic, event) 메서드 Mocking
            Mockito.doNothing()
                    .when(mockProducer)
                    .send(Mockito.anyString(), Mockito.any());

            // 키가 있는 send(topic, key, event) 메서드 Mocking
            Mockito.doNothing()
                    .when(mockProducer)
                    .send(Mockito.anyString(), Mockito.anyString(), Mockito.any());

            return mockProducer;
        }
    }
}
