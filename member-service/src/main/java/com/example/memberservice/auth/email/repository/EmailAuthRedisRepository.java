package com.example.memberservice.auth.email.repository;

import com.example.memberservice.common.redis.model.enums.RedisKeyPrefix;
import com.example.memberservice.common.redis.repository.KeyValueRepository;
import com.example.memberservice.member.model.enums.MemberRole;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailAuthRedisRepository implements EmailAuthRepository{

    private final KeyValueRepository keyValueRepository;

    @Value("${mail.ttl.auth-code}")
    private long authCodeExpirationMinute;

    @Value("${mail.ttl.verified}")
    private long verifiedExpirationMinute;


    public void saveAuthCode(MemberRole memberRole, String memberCode, String authCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFICATION_CODE);
        keyValueRepository.setSingleData(key, authCode, authCodeExpirationMinute);
    }

    public void saveAuthVerification(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        keyValueRepository.setSingleData(key, "verify", verifiedExpirationMinute);
    }

    public Optional<String> findAuthCodeByMemberCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFICATION_CODE);
        return keyValueRepository.getSingleData(key);
    }

    public Optional<String> findVerificationByMemberCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        return keyValueRepository.getSingleData(key);
    }

    @Override
    public boolean existVerificationByMemberCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        Optional<String> optionalObject = keyValueRepository.getSingleData(key);

        return optionalObject.isPresent();
    }

    public boolean deleteAuthCode(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFICATION_CODE);
        return keyValueRepository.deleteSingleData(key);
    }

    public boolean deleteVerified(MemberRole role, String memberCode) {
        String key = buildKey(role, memberCode, RedisKeyPrefix.EMAIL_VERIFIED);
        return keyValueRepository.deleteSingleData(key);
    }

    public Long incrementRetryCount(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED_COUNT);
        return keyValueRepository.incrementKey(key, authCodeExpirationMinute);
    }

    public boolean deleteRetryCount(MemberRole memberRole, String memberCode) {
        String key = buildKey(memberRole, memberCode, RedisKeyPrefix.EMAIL_VERIFIED_COUNT);
        return keyValueRepository.deleteSingleData(key);
    }

    private String buildKey(MemberRole memberRole, String memberCode,
        RedisKeyPrefix redisKeyPrefix) {
        return redisKeyPrefix.build(String.format("%s:%s", memberRole.toString(), memberCode));
    }

}
