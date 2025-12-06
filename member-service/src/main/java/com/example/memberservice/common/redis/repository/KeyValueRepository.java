package com.example.memberservice.common.redis.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueRepository {

    void setSingleData(String key, Object value, long offset); // Redis 단일 데이터 값을 등록/수정합니다.

    Optional<String> getSingleData(String key); // Redis 키를 기반으로 단일 데이터의 값을 조회합니다.

    boolean deleteSingleData(String key);// Redis 키를 기반으로 단일 데이터의 값을 삭제합니다.

    Long incrementKey(String key, long offset);// 특정 Redis 키의 value를 1 증가 시키고 해당 증가 된 값을 반환 받는다.
}
