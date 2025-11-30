package com.example.contractservice.contract.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "commissions_capacity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommissionsCapacity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commission_code", nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private String commissionCode;

    @Column(name = "recruit_capacity", nullable = false)
    private int recruitCapacity;

    @Column(name = "recruited_count", nullable = false)
    private int recruitedCount;

    @Column(name = "selection_capacity", nullable = false)
    private int selectionCapacity;

    @Column(name = "selected_count", nullable = false)
    private int selectedCount;

}
