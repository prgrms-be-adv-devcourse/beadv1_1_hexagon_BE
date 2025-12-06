package com.example.memberservice.auth.token.repository;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.redis.model.enums.RedisKeyPrefix;
import com.example.memberservice.common.redis.repository.RedisSingleDataRepository;
import com.example.memberservice.common.security.jwt.JwtProperties;
import com.example.memberservice.member.model.enums.MemberRole;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedisSingleDataRepository redisSingleDataRepository;

    private final JwtProperties jwtProperties;

    //save
    public boolean saveRefreshToken(String memberCode, String refreshToken) {
        String key = buildKey(memberCode);

        redisSingleDataRepository.setSingleData(key, refreshToken,
            jwtProperties.getRefreshTokenTtl());

        return true;
    }

    //find
    public Optional<String> findRefreshTokenByMemberCode(String memberCode) {
        String key = buildKey(memberCode);

        return redisSingleDataRepository.getSingleData(key);
    }

    //delete
    public boolean deleteRefreshTokenByMemberCode(String memberCode) {
        String key = buildKey(memberCode);

        return redisSingleDataRepository.deleteSingleData(key);
    }

    private String buildKey(String memberCode) {
        RedisKeyPrefix redisKeyPrefix = RedisKeyPrefix.TOKEN;
        return redisKeyPrefix.build(memberCode);
    }

}
