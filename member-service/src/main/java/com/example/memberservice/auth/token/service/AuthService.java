package com.example.memberservice.auth.token.service;

import com.example.memberservice.auth.token.repository.RefreshTokenRedisRepository;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.security.jwt.JwtProperties;
import com.example.memberservice.common.security.jwt.JwtTokenGenerator;
import com.example.memberservice.common.security.jwt.JwtTokenParser;
import com.example.memberservice.common.security.jwt.JwtTokenValidator;
import com.example.memberservice.member.repository.MemberJpaRepository;
import com.example.memberservice.auth.token.service.dto.output.TokensOutput;
import io.jsonwebtoken.Claims;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    private final MemberJpaRepository memberJpaRepository;

    private final JwtTokenValidator jwtTokenValidator;

    private final JwtTokenGenerator jwtTokenGenerator;

    private final JwtTokenParser jwtTokenParser;

    @Transactional
    public TokensOutput reissueAccessTokenByRefreshToken(String refreshToken) {
        String memberCode = getMemberCode(refreshToken);

        String newRefreshToken = jwtTokenGenerator.generateRefreshToken(memberCode);

        refreshTokenRedisRepository.saveRefreshToken(memberCode, newRefreshToken);

        boolean isSignedUp = memberJpaRepository.existsByCode(memberCode);

        String newAccessToken = jwtTokenGenerator.generateAccessToken(memberCode, isSignedUp);

        return new TokensOutput(newAccessToken, newRefreshToken);

    }

    @Transactional
    public void deleteRefreshTokenToRedis(String refreshToken) {
        String memberCode = getMemberCode(refreshToken);

        refreshTokenRedisRepository.deleteRefreshTokenByMemberCode(memberCode);
    }

    private String getMemberCode(String refreshToken) {
        Claims claims = jwtTokenValidator.validateRefreshToken(refreshToken);

        String memberCode = jwtTokenParser.parseMemberCode(claims);

        Optional<String> optionalExistRefreshToken = refreshTokenRedisRepository.findRefreshTokenByMemberCode(memberCode);

        String existRefreshToken = optionalExistRefreshToken.orElseThrow(
            () -> new BusinessException(ErrorCode.UNAUTHORIZATION));

        if (!existRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZATION);
        }
        return memberCode;
    }
}
