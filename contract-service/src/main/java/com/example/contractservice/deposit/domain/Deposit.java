package com.example.contractservice.deposit.domain;

import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.INVALID_AMOUNT;
import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.NOT_ENOUGH_AMOUNT;

import com.example.contractservice.deposit.domain.exception.DepositException;
import java.time.Instant;
import java.util.UUID;

public class Deposit {

    private String code;

    private String memberCode;

    private Instant createdAt;
    private Instant updatedAt;

    private Long amount;

    public static Deposit createdBy(String memberCode) {
        return new Deposit(null, null, null, memberCode, 0L);
    }

    public Deposit(String code, Instant createdAt, Instant updatedAt, String memberCode, Long amount) {
        this.code = (code == null) ? generateCode() : code;
        this.memberCode = memberCode;
        this.amount = amount;
        this.createdAt = (createdAt == null) ? Instant.now() : createdAt;
        this.updatedAt = (updatedAt == null) ? this.createdAt : updatedAt;
    }

    public void withdraw(Long amount) {
        if (this.amount < amount) {
            throw new DepositException(NOT_ENOUGH_AMOUNT);
        }

        this.amount -= amount;
    }

    public void transfer(Long amount) {
        if (amount < 0) {
            throw new DepositException(INVALID_AMOUNT);
        }

        this.amount += amount;
    }

    public String getCode() {
        return code;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public Long getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

}
