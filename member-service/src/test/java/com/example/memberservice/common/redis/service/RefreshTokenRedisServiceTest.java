package com.example.memberservice.common.redis.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.memberservice.auth.token.repository.RefreshTokenRedisRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RefreshTokenRedisServiceTest {

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;


    @Test
    @DisplayName("레디스 데이터 생성 확인")
    void redisConnectTest() {
        //Given
        String key = "key123";

        String value = "value123";

        //When
        refreshTokenRedisRepository.setSingleData(key, value,50000);

        //Then
        Optional<String> saveData = refreshTokenRedisRepository.getSingleData(key);

        assertEquals("value123", saveData.orElse("FAIL"));

        refreshTokenRedisRepository.deleteSingleData(key);

        Optional<String> deletedData = refreshTokenRedisRepository.getSingleData(key);

        assertEquals("FAIL", deletedData.orElse("FAIL"));
    }
}