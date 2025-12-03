package com.example.memberservice.common.redis.service;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface RedisSingleDataRepository {


    void setSingleData(String key, Object value,
        long refreshTokenTtl); // Redis 단일 데이터 값을 등록/수정합니다. 이때 단일값은 JWT 설정에 따라 지정

    Optional<String> getSingleData(String key); // Redis 키를 기반으로 단일 데이터의 값을 조회합니다.

    boolean deleteSingleData(String key);  // Redis 키를 기반으로 단일 데이터의 값을 삭제합니다.
}
