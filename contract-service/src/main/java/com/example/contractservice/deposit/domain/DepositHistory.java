package com.example.contractservice.deposit.domain;

import com.example.contractservice.deposit.domain.vo.DepositChange;
import java.time.Instant;
import java.util.UUID;

public class DepositHistory {
    private String code;
    private String depositCode;
    private String contractCode;

    private DepositChange depositChange;

    private String summary;

    private Instant createdAt;

    public DepositHistory(String depositCode, String contractCode, DepositChange depositChange, String summary) {
        this(null, depositCode, contractCode, depositChange, summary, null);
    }

    public DepositHistory(String code, String depositCode, String contractCode, DepositChange depositChange,
        String summary, Instant createdAt) {
        this.code = (code == null) ? generateCode() : code;
        this.depositCode = depositCode;
        this.contractCode = contractCode;
        this.depositChange = depositChange;
        this.summary = summary;
        this.createdAt = (createdAt == null) ? Instant.now() : createdAt;
    }

    public String getCode() {
        return code;
    }

    public String getDepositCode() {
        return depositCode;
    }

    public String getContractCode() {
        return contractCode;
    }

    public DepositChange getDepositChange() {
        return depositChange;
    }

    public String getSummary() {
        return summary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }
}
