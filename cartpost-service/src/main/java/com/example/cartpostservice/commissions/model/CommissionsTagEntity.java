package com.example.cartpostservice.commissions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "commissions_tags")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommissionsTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String commissionCode;

    @Column(nullable = false)
    private String tagCode;

    @Builder
    public CommissionsTagEntity(String commissionCode, String tagCode) {
        this.commissionCode = commissionCode;
        this.tagCode = tagCode;
    }
}
