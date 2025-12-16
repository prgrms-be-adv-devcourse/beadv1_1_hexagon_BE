package com.example.contractservice.settlement.service.batch.writer;

import com.example.contractservice.common.util.StringUtil;
import com.example.contractservice.deposit.domain.Deposit;
import com.example.contractservice.deposit.domain.DepositHistory;
import com.example.contractservice.deposit.domain.vo.DepositChange;
import com.example.contractservice.deposit.repository.DepositRepository;
import com.example.contractservice.deposit.repository.batch.DepositBatchRepository;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.repository.batch.SettlementBatchRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class SettlementCustomWriter implements ItemWriter<Settlement> {

    private final SettlementBatchRepository settlementBatchRepository;
    private final DepositRepository depositRepository;
    private final DepositBatchRepository depositBatchRepository;

    @Value("${batch.settlement.settlement-rate}")
    private BigDecimal settlementRate;
    @Value("${admin.member.code}")
    private String adminMemberCode;

    @Override
    public void write(@NonNull Chunk<? extends Settlement> chunk) {
        log.info("write 시작!");
        List<Settlement> settlements = (List<Settlement>) chunk.getItems();

        Map<String, Deposit> memberDepositMap = new HashMap<>(); // 벌크 처리할 예치금 데이터
        List<DepositHistory> depositHistories = new ArrayList<>(); // 벌크 처리할 예치금 히스토리

        initAdmin(memberDepositMap);
        Deposit adminDeposit = memberDepositMap.get(adminMemberCode);

        for (Settlement settlement : settlements) {
            settlement.settle(settlementRate);

            String receiverCode = settlement.getSettlementReference().receiverCode();
            Deposit receiverDeposit = getReceiverDeposit(receiverCode, memberDepositMap);

            wireTransferToReceiver(adminDeposit, receiverDeposit, settlement, depositHistories);
        }

        settlementBatchRepository.updateAllInBatch(settlements);
        depositBatchRepository.updateAllDeposits(memberDepositMap);
        depositBatchRepository.saveAllHistories(depositHistories);

        log.info("write 종료!");
    }

    private void wireTransferToReceiver(Deposit adminDeposit, Deposit receiverDeposit, Settlement settlement, List<DepositHistory> depositHistories) {
        Long settledAmount = settlement.getSettlementStatusInfo().settledAmount();

        adminDeposit.withdraw(settledAmount);
        receiverDeposit.transfer(settledAmount);

        depositHistories.add(new DepositHistory(adminDeposit.getCode(), settlement.getSettlementReference().contractCode(), new DepositChange(-settledAmount, adminDeposit.getAmount()),
                StringUtil.format("정산 코드 {}에 대한 정산 출금", settlement.getCode())));
        depositHistories.add(new DepositHistory(receiverDeposit.getCode(), settlement.getSettlementReference().contractCode(), new DepositChange(settledAmount, receiverDeposit.getAmount()),
                StringUtil.format("정산 코드 {}에 대한 정산 입금", settlement.getCode())));
    }

    private Deposit getReceiverDeposit(String receiverCode, Map<String, Deposit> memberDepositMap) {
        memberDepositMap.computeIfAbsent(receiverCode, depositRepository::findDepositByMemberCode);

        return memberDepositMap.get(receiverCode);
    }

    private void initAdmin(Map<String, Deposit> memberDepositMap) {
        memberDepositMap.put(adminMemberCode, depositRepository.findDepositByMemberCode(adminMemberCode));
    }
}
