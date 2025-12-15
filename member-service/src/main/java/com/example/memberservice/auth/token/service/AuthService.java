package com.example.memberservice.auth.token.service;

import com.example.memberservice.auth.token.repository.RefreshTokenRepository;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.security.jwt.JwtTokenGenerator;
import com.example.memberservice.common.security.jwt.JwtTokenParser;
import com.example.memberservice.common.security.jwt.JwtTokenValidator;
import com.example.memberservice.member.model.entity.Members;
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

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberJpaRepository memberJpaRepository;

    private final JwtTokenValidator jwtTokenValidator;

    private final JwtTokenGenerator jwtTokenGenerator;

    private final JwtTokenParser jwtTokenParser;

    @Transactional
    public TokensOutput reissueAccessTokenByRefreshToken(String refreshToken) {
        String memberCode = getMemberCode(refreshToken);

        String newRefreshToken = jwtTokenGenerator.generateRefreshToken(memberCode);

        refreshTokenRepository.saveRefreshToken(memberCode, newRefreshToken);

        Optional<Members> optionalMembers = memberJpaRepository.findByCode(memberCode);


        String newAccessToken = optionalMembers
            .map(members -> // Optional 안에 값이 존재할 때 실행할 로직
                jwtTokenGenerator.generateAccessToken(members.getCode(), true, members.getRole()))
            .orElseGet(() -> // Optional 안에 값이 존재하지 않을 때 실행할 로직
                jwtTokenGenerator.generateAccessToken(memberCode, false));

        return new TokensOutput(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void deleteRefreshTokenToRedis(String refreshToken) {
        String memberCode = getMemberCode(refreshToken);

        refreshTokenRepository.deleteRefreshTokenByMemberCode(memberCode);
    }

    private String getMemberCode(String refreshToken) {
        Claims claims = jwtTokenValidator.validateRefreshToken(refreshToken);

        String memberCode = jwtTokenParser.parseMemberCode(claims);

        Optional<String> optionalExistRefreshToken = refreshTokenRepository.findRefreshTokenByMemberCode(memberCode);

        String existRefreshToken = optionalExistRefreshToken.orElseThrow(
            () -> new BusinessException(ErrorCode.UN_AUTHORIZATION));

        if (!existRefreshToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.UN_AUTHORIZATION);
        }
        return memberCode;
    }
}
