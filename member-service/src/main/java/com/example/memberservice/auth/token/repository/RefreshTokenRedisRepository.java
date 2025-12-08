package com.example.memberservice.auth.token.repository;

import com.example.memberservice.common.redis.model.enums.RedisKeyPrefix;
import com.example.memberservice.common.redis.repository.KeyValueRepository;
import com.example.memberservice.common.security.jwt.JwtProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository implements RefreshTokenRepository{

    private final KeyValueRepository keyValueRepository;

    private final JwtProperties jwtProperties;

    //save
    public boolean saveRefreshToken(String memberCode, String refreshToken) {
        String key = buildKey(memberCode);

        keyValueRepository.setSingleData(key, refreshToken,
            jwtProperties.getRefreshTokenTtl());

        return true;
    }

    //find
    public Optional<String> findRefreshTokenByMemberCode(String memberCode) {
        String key = buildKey(memberCode);

        return keyValueRepository.getSingleData(key);
    }

    //delete
    public boolean deleteRefreshTokenByMemberCode(String memberCode) {
        String key = buildKey(memberCode);

        return keyValueRepository.deleteSingleData(key);
    }

    private String buildKey(String memberCode) {
        RedisKeyPrefix redisKeyPrefix = RedisKeyPrefix.TOKEN;
        return redisKeyPrefix.build(memberCode);
    }

}
