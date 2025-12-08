package com.example.memberservice.auth.token.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository {
    boolean saveRefreshToken(String memberCode, String refreshToken);

    Optional<String> findRefreshTokenByMemberCode(String memberCode);

    boolean deleteRefreshTokenByMemberCode(String memberCode);
}
