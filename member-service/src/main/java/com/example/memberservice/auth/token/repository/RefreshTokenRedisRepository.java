package com.example.memberservice.auth.token.repository;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.redis.model.enums.RedisKeyPrefix;
import com.example.memberservice.common.redis.repository.RedisSingleDataRepository;
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

    private final RedisKeyPrefix redisKeyPrefix = RedisKeyPrefix.TOKEN;

    @Override
    public void setSingleData(String key, Object value, long refreshTokenTTL) {
        Duration duration = Duration.ofMillis(refreshTokenTTL);

        this.executeOperation(key,
            () -> valueOperations().set(redisKeyPrefix.build(key), value, duration));
    }

    @Override
    public Optional<String> getSingleData(String key) {

        Object value = valueOperations().get(redisKeyPrefix.build(key));

        return Optional.ofNullable(value).map(Object::toString);
    }

    @Override
    public boolean deleteSingleData(String key) {

        Boolean result = redisTemplate.delete(redisKeyPrefix.build(key));

        return Boolean.TRUE.equals(result);
    }

    private ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }

    private void executeOperation(String key, Runnable operation) {
        try {
            operation.run();
            log.info("redis에 정상 저장하였습니다.");
        } catch (Exception e) {
            log.error("Redis 저장 실패: key={}, cause={}", redisKeyPrefix.build(key), e.getMessage(),
                e);
            throw new BusinessException(ErrorCode.DATA_SAVE_FAILED);
        }
    }

}
