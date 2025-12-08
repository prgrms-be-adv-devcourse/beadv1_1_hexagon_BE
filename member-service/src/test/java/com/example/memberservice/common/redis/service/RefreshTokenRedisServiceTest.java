package com.example.memberservice.common.redis.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.example.memberservice.auth.token.repository.RefreshTokenRedisRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

//@SpringBootTest
class RefreshTokenRedisServiceTest {

//    @MockitoBean
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

//    @Test
//    @DisplayName("레디스 데이터 생성 확인")
    void redisConnectTest() {
        //Given
        String key = "key123";

        String value = "value123";

        //When
        when(refreshTokenRedisRepository.findRefreshTokenByMemberCode(key)).thenReturn(Optional.of(value));
        refreshTokenRedisRepository.saveRefreshToken(key, value);

        //Then
        Optional<String> saveData = refreshTokenRedisRepository.findRefreshTokenByMemberCode(key);

        assertEquals("value123", saveData.orElse("FAIL"));

        refreshTokenRedisRepository.deleteRefreshTokenByMemberCode(key);

        when(refreshTokenRedisRepository.findRefreshTokenByMemberCode(key)).thenReturn(Optional.empty());

        Optional<String> deletedData = refreshTokenRedisRepository.findRefreshTokenByMemberCode(key);

        assertEquals("FAIL", deletedData.orElse("FAIL"));
    }
}