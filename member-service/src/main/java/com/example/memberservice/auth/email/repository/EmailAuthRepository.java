package com.example.memberservice.auth.email.repository;

import com.example.memberservice.member.model.enums.MemberRole;
import java.util.Optional;

public interface EmailAuthRepository {

    void saveAuthCode(MemberRole memberRole, String memberCode, String authCode);

    void saveAuthVerification(MemberRole memberRole, String memberCode);

    Optional<String> findAuthCodeByMemberCode(MemberRole memberRole, String memberCode);

    Optional<String> findVerificationByMemberCode(MemberRole memberRole, String memberCode);

    boolean deleteAuthCode(MemberRole memberRole, String memberCode);

    boolean deleteVerified(MemberRole role, String memberCode);

    Long incrementRetryCount(MemberRole memberRole, String memberCode);

    boolean deleteRetryCount(MemberRole memberRole, String memberCode);
}
