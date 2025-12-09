package com.example.memberservice.socialmember.entity;

import com.example.memberservice.common.security.model.vo.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "social_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)

public class SocialMembers {

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

    @Column(nullable = false, unique = true)
    @Comment("이메일")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("OAuth 제공자")
    private Provider provider;

    @Column(name = "provider_id", nullable = false)
    @Comment("OAuth 서버 제공 ID")
    private String providerId;

    @Builder
    private SocialMembers(String email, Provider provider, String providerId) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;

        this.code = UUID.randomUUID().toString();
        this.isDeleted = false;
    }

}
