package com.example.memberservice.auth.email.repository;

import com.example.memberservice.common.kafka.producer.MemberEventProducer;
import com.example.memberservice.common.redis.model.enums.RedisKeyPrefix;
import com.example.memberservice.common.redis.repository.RedisSingleDataRepository;
import com.example.memberservice.member.model.enums.MemberRole;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthRedisService {

    private final RedisSingleDataRepository redisSingleDataRepository;
    private final MemberEventProducer memberEventProducer;

    @Value("${mail.ttl.auth-code}")
    private long authCodeExpirationMinute;

    @Value("${mail.ttl.verified}")
    private long verifiedExpirationMinute;


    public void createAuthCode(MemberRole memberRole, String memberCode, String authCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFICATION_CODE);
        redisSingleDataRepository.setSingleData(key, authCode, authCodeExpirationMinute);
    }

    public void createAuthVerification(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        redisSingleDataRepository.setSingleData(key, true, verifiedExpirationMinute);
    }

    public Optional<String> findAuthCodeByMemberCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFICATION_CODE);
        return redisSingleDataRepository.getSingleData(key);
    }

    public Optional<String> findVerificationByMemberCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        return redisSingleDataRepository.getSingleData(key);
    }


    public boolean deleteAuthCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFICATION_CODE);
        return redisSingleDataRepository.deleteSingleData(key);
    }

    public boolean deleteVerified(MemberRole role, String memberCode) {
        String key = buildKey(role, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        return redisSingleDataRepository.deleteSingleData(key);
    }

    private String buildKey(MemberRole memberRole, String memberCode,
        RedisKeyPrefix redisKeyPrefix) {
        return redisKeyPrefix.build(String.format("%s:%s", memberRole.toString(), memberCode));
    }
}
