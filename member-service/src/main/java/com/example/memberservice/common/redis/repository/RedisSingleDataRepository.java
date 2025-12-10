package com.example.memberservice.common.redis.repository;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisSingleDataRepository implements KeyValueRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final DefaultRedisScript<Long> INCR_WITH_TTL_SCRIPT =
        new DefaultRedisScript<>(
            "local v = redis.call('INCR', KEYS[1]); " +
                "if v == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; " +
                "return v;",
            Long.class
        );

    @Override
    public void setSingleData(String key, Object value, long offset) {
        Duration duration = Duration.ofMinutes(offset);

        this.executeOperation(key,() -> valueOperations().set(key, value, duration));
    }

    @Override
    public Optional<String> getSingleData(String key) {
        Object value = valueOperations().get(key);

        return Optional.ofNullable(value).map(Object::toString);
    }

    @Override
    public boolean deleteSingleData(String key) {

        Boolean result = redisTemplate.delete(key);

        return Boolean.TRUE.equals(result);
    }

    @Override
    public Long incrementKey(String key, long offset) {
        long ttlSeconds = Duration.ofMinutes(offset).toSeconds();

        return redisTemplate.execute(
            INCR_WITH_TTL_SCRIPT,
            List.of(key),
            String.valueOf(ttlSeconds)
        );
    }

    private ValueOperations<String, Object> valueOperations() {
        return redisTemplate.opsForValue();
    }

    private void executeOperation(String key, Runnable operation) {
        try {
            operation.run();
            log.info("redis에 정상 저장하였습니다.");
        } catch (Exception e) {
            log.error("Redis 저장 실패: key={}, cause={}", key, e.getMessage(), e);
            throw new BusinessException(ErrorCode.DATA_SAVE_FAILED);
        }
    }
}
