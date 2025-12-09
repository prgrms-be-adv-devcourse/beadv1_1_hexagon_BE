package com.example.memberservice.member.model.entity;


import com.example.memberservice.member.model.enums.Gender;

import com.example.memberservice.common.security.model.vo.Provider;
import com.example.memberservice.member.model.enums.MemberRole;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.hibernate.annotations.Comment;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Members {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    @Comment("외부 노출용 식별자(UUID)")
    private String code;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성 일시")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Comment("삭제 여부")
    private Boolean isDeleted = false;

    @Column(nullable = false)
    @Comment("닉네임")
    private String nickName;

    @Column(nullable = false)
    @Comment("이메일")
    private String email;

    @Column(name = "phone_number", nullable = false)
    @Comment("핸드폰 번호")
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    @Comment("생년월일")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("성별")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("OAuth 제공자")
    private Provider provider;

    @Column(name = "provider_id", nullable = false)
    @Comment("OAuth 서버 제공 ID")
    private String providerId;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Builder
    private Members(String nickName, String code, String email, String phoneNumber,
        LocalDate birthDate, Gender gender,
        Provider provider, String providerId, MemberRole role) {
        this.nickName = nickName;
        this.code = code;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.gender = gender;
        this.provider = provider;
        this.providerId = providerId;
        if(role==null){
            this.role = MemberRole.NONE;
        }else{
            this.role = role;
        }
    }

    // 닉네임 변경
    public void updateNickName(String newNickName) {
        this.nickName = newNickName;
    }

//    //판매자 등록 가능 여부 확인
//    public boolean canEnableWork() {
//        return Boolean.FALSE.equals(this.canWork);
//    }
//
//    // 판매자 등록 변경
//    public void updateCanWork(Boolean canWork) {
//        this.canWork = canWork;
//    }

    public boolean canRegisterRoleState(MemberRole role) {
        switch (role) {
            case CLIENT -> {
                return canRegisterClientState();
            }
            case FREELANCER -> {
                return canRegisterFreelancerState();
            }
            default -> {
                return false;
            }
        }
    }

    public boolean canDeleteRoleState(MemberRole role) {
        switch (role) {
            case CLIENT -> {
                return canDeleteClientState();
            }
            case FREELANCER -> {
                return canDeleteFreelancerState();
            }
            default -> {
                return false;
            }
        }
    }

    public void registerRoleState(MemberRole role) {
        switch (role) {
            case CLIENT -> {
                registerClientState();
            }
            case FREELANCER -> {
                registerFreelancerState();
            }
        }
    }

    public void deleteRoleState(MemberRole role) {
        switch (role) {
            case CLIENT -> {
                deleteClientState();
            }
            case FREELANCER -> {
                deleteFreelancerState();
            }
        }
    }

    // 핸드폰 번호 변경
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    // 생년월일 변경

    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    // 성별 변경

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void deletedMember() {
        this.isDeleted = true;
    }

    public void restoreMember() {
        this.isDeleted = false;
    }

    private boolean canRegisterClientState() {
        return (this.role.equals(MemberRole.NONE) || this.role.equals(MemberRole.FREELANCER));
    }

    private boolean canRegisterFreelancerState() {
        return (this.role.equals(MemberRole.NONE) || this.role.equals(MemberRole.CLIENT));
    }

    private boolean canDeleteClientState() {
        return (this.role.equals(MemberRole.CLIENT) || this.role.equals(MemberRole.BOTH));
    }

    private boolean canDeleteFreelancerState() {
        return (this.role.equals(MemberRole.FREELANCER) || this.role.equals(MemberRole.BOTH));
    }

    private void registerClientState() {
        if (this.role.equals(MemberRole.FREELANCER)) {
            this.role = MemberRole.BOTH;
        } else if (this.role.equals(MemberRole.NONE)) {
            this.role = MemberRole.CLIENT;
        }
    }

    private void registerFreelancerState() {
        if (this.role.equals(MemberRole.CLIENT)) {
            this.role = MemberRole.BOTH;
        } else if (this.role.equals(MemberRole.NONE)) {
            this.role = MemberRole.FREELANCER;
        }
    }

    private void deleteClientState() {
        if (this.role.equals(MemberRole.CLIENT)) {
            this.role = MemberRole.NONE;
        } else if (this.role.equals(MemberRole.BOTH)) {
            this.role = MemberRole.FREELANCER;
        }
    }

    private void deleteFreelancerState() {
        if (this.role.equals(MemberRole.FREELANCER)) {
            this.role = MemberRole.NONE;
        } else if (this.role.equals(MemberRole.BOTH)) {
            this.role = MemberRole.CLIENT;
        }
    }
}
