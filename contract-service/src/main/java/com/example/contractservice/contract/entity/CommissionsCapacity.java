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

    @Column(name = "apply_capacity", nullable = false)
    private int applyCapacity;

    @Column(name = "applied_count", nullable = false)
    private int appliedCount;

    @Column(name = "selection_capacity", nullable = false) // 최대 선정 인원
    private int selectionCapacity;

    @Column(name = "selected_count", nullable = false)
    private int selectedCount;

    private CommissionsCapacity(String commissionCode, int applyCapacity, int appliedCount, int selectionCapacity,
            int selectedCount) {
        this.commissionCode = commissionCode;
        this.applyCapacity = applyCapacity;
        this.appliedCount = appliedCount;
        this.selectionCapacity = selectionCapacity;
        this.selectedCount = selectedCount;
    }

    public static CommissionsCapacity createBy(String commissionCode, int applyCapacity, int selectionCapacity) {
        return new CommissionsCapacity(commissionCode,
                applyCapacity,
                0,
                selectionCapacity,
                0);
    }

    public void increaseAppliedCount() {
        this.appliedCount++;
    }

    public void increaseSelectedCount() {
        this.selectedCount++;
    }
}
