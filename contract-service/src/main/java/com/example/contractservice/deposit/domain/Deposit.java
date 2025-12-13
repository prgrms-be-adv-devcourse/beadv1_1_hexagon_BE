package com.example.contractservice.deposit.domain;

import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.INVALID_AMOUNT;
import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.NOT_ENOUGH_AMOUNT;

import com.example.contractservice.deposit.domain.exception.DepositException;
import java.util.UUID;

public class Deposit {

    private String code;

    private String memberCode;

    private Long amount;

    public static Deposit createdBy(String memberCode) {
        return new Deposit(null, memberCode, 0L);
    }

    public Deposit(String code, String memberCode, Long amount) {
        this.code = (code == null) ? generateCode() : code;
        this.memberCode = memberCode;
        this.amount = amount;
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

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

}
