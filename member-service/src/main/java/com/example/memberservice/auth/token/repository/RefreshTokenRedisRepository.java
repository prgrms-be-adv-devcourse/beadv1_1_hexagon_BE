package com.example.memberservice.auth.token.repository;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.redis.service.RedisSingleDataRepository;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenRedisRepository implements RedisSingleDataRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String REDIS_KEY_PREFIX = "TOKEN:";

    @Override
    public void setSingleData(String key, Object value, long refreshTokenTTL) {
        Duration duration = Duration.ofMillis(refreshTokenTTL);

        this.executeOperation(() -> valueOperations().set(buildKey(key), value, duration));
    }

    @Override
    public Optional<String> getSingleData(String key) {

        Object value = valueOperations().get(buildKey(key));

        return Optional.ofNullable(value).map(Object::toString);
    }

    @Override
    public boolean deleteSingleData(String key) {

        Boolean result = redisTemplate.delete(buildKey(key));

        return Boolean.TRUE.equals(result);
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
