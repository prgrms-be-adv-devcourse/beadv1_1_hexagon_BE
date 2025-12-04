package com.example.memberservice.common.redis.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface RedisSingleDataRepository {

    void setSingleData(String key, Object value, long offset); // Redis 단일 데이터 값을 등록/수정합니다.

    Optional<String> getSingleData(String key); // Redis 키를 기반으로 단일 데이터의 값을 조회합니다.

    boolean deleteSingleData(String key);  // Redis 키를 기반으로 단일 데이터의 값을 삭제합니다.
}
