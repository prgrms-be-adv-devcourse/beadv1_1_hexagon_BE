package com.example.contractservice.contract.domain;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.domain.vo.ContractContent;
import com.example.contractservice.contract.domain.vo.ContractInfo;
import java.time.Instant;
import java.util.UUID;

public class Contract {

    private String code;

    private ContractInfo info;
    private ContractContent content;

    private Instant createdAt;
    private Instant updatedAt;

    public Contract(String code, ContractInfo info, ContractContent content, Instant createdAt,
        Instant updatedAt) {
        this.code = (code == null) ? generateCode() : code;
        this.info = info;
        this.content = content;
        this.createdAt = (createdAt == null) ? Instant.now() : createdAt;
        this.updatedAt = (updatedAt == null) ? this.createdAt : updatedAt;
    }

    public Contract(ContractInfo info, ContractContent content, Instant createdAt, Instant updatedAt) {
        this(null, info, content, createdAt, updatedAt);
    }

    private String generateCode() {
        return UUID.randomUUID().toString();
    }

    public String getCode() {
        return code;
    }

    public ContractInfo getInfo() {
        return info;
    }

    public ContractContent getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean canUserPay(String userCode) { // 유저가 클라이언트인지 확인
        return info.clientCode().equals(userCode);
    }

    public void pay() {
        this.info = this.info.pay();
    }

    public boolean isRequested() {
        return getInfo().status() == ContractStatus.REQUESTED;
    }
}
