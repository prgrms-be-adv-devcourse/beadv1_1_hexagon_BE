package com.example.memberservice.auth.email.repository;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.redis.repository.RedisSingleDataRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAuthRedisRepository implements RedisSingleDataRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String REDIS_KEY_PREFIX = "EMAIL:";

    @Override
    public void setSingleData(String key, Object value, long emailAuthTtl) {

    }

    @Override
    public Optional<String> getSingleData(String key) {
        return Optional.empty();
    }

    @Override
    public boolean deleteSingleData(String key) {
        return false;
    }

    private ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }

    private ListOperations<String, Object> listOperations() {
        return redisTemplate.opsForList();
    }



    private void executeOperation(Runnable operation) {
        try {
            operation.run();
            log.info("redis에 정상 저장하였습니다.");
        } catch (Exception e) {
            log.info("Redis에 정상 저장되지 못했습니다.");
            throw new BusinessException(ErrorCode.DATA_SAVE_FAILED);
        }
    }

    private String buildKey(String key) {
        return String.format("%s%s", REDIS_KEY_PREFIX, key);
    }
}
