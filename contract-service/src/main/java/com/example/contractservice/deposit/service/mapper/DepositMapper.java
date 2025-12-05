package com.example.contractservice.deposit.service.mapper;

import com.example.contractservice.deposit.domain.Deposit;
import com.example.contractservice.deposit.entity.DepositEntity;

public abstract class DepositMapper {

    private DepositMapper() {}

    public static Deposit toDomain(DepositEntity depositEntity) {
        return new Deposit(
                depositEntity.getCode(),
                depositEntity.getMemberCode(),
                depositEntity.getAmount());
    }

    public static void applyToEntity(Deposit deposit, DepositEntity depositEntity) {
        depositEntity.updateInfo(deposit.getAmount());
    }

    public static DepositEntity toEntity(Deposit deposit) {
        return DepositEntity.createBy(deposit.getMemberCode());
    }
}
