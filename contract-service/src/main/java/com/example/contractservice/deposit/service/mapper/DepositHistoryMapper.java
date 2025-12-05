package com.example.contractservice.deposit.service.mapper;

import com.example.contractservice.deposit.domain.DepositHistory;
import com.example.contractservice.deposit.domain.vo.DepositChange;
import com.example.contractservice.deposit.entity.DepositHistoryEntity;

public abstract class DepositHistoryMapper {

    private DepositHistoryMapper() {}

    public static DepositHistoryEntity toEntity(DepositHistory depositHistory) {
        DepositChange depositChange = depositHistory.getDepositChange();

        return DepositHistoryEntity.builder()
                .code(depositHistory.getCode())
                .depositCode(depositHistory.getDepositCode())
                .changeAmount(depositChange.changeAmount())
                .resultAmount(depositChange.resultAmount())
                .summary(depositHistory.getSummary())
                .build();
    }

    public static DepositHistory toDomain(DepositHistoryEntity historyEntity) {
        DepositChange depositChange = new DepositChange(historyEntity.getChangeAmount(), historyEntity.getResultAmount());

        return new DepositHistory(historyEntity.getCode(), depositChange, historyEntity.getSummary());
    }
}
