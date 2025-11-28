package com.example.memberservice.common.redis.service;

import static org.junit.jupiter.api.Assertions.*;

import com.netflix.discovery.converters.Auto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
class RefreshTokenRedisServiceTest {

    @Autowired
    private RedisSingleDataService redisSingleDataService;


    @Test
    @DisplayName("레디스 데이터 생성 확인")
    void redisConnectTest() {
        //Given
        String key = "key123";

        String value = "value123";

        //When
        redisSingleDataService.setSingleData(key, value,50000);

        //Then
        Optional<String> saveData = redisSingleDataService.getSingleData(key);

        assertEquals("value123", saveData.orElse("FAIL"));

        redisSingleDataService.deleteSingleData(key);

        Optional<String> deletedData = redisSingleDataService.getSingleData(key);

        assertEquals("FAIL", deletedData.orElse("FAIL"));
    }
}