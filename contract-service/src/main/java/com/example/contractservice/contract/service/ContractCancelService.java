package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.CANCEL_NOT_AVAILABLE;

import com.example.contractservice.contract.controller.dto.response.CommissionCapacityResponse;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.deposit.controller.dto.response.DepositHistoryInfo;
import com.example.contractservice.deposit.service.DepositService;
import com.example.contractservice.deposit.service.dto.request.DepositProcessRequest;
import com.example.contractservice.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.contract.CommissionOpenCloseEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractCancelService {

    private final DepositService depositService;
    private final SettlementService settlementService;
    private final ContractRepository contractRepository;
    private final CommissionsCapacityService commissionsCapacityService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${admin.member.code}")
    private String adminMemberCode;

    @Transactional
    public void processCancel(Contract contract) {

        if (!(contract.isRequested() || contract.isPaid())) {
            throw new ContractException(CANCEL_NOT_AVAILABLE);
        }

        if (contract.isPaid()) {
            rollbackPaidContract(contract);
        }

        contract.cancel();

        contractRepository.saveContract(contract);
    }

    private void rollbackPaidContract(Contract contract) {
        publishEventIfCommissionFull(contract); // 의뢰글 마감 상태였다면 의뢰글 오픈 이벤트 발행
        refund(contract); // 환불
        removeSettlements(contract); // 정산 데이터 제거
    }

    private void publishEventIfCommissionFull(Contract contract) {
        String commissionCode = contract.getInfo().commissionCode();
        CommissionCapacityResponse capacityResponse = commissionsCapacityService.getCapacity(commissionCode);

        if (capacityResponse.selectionCapacity() == capacityResponse.selectedCapacity()) { // 의뢰글 마감 상황이었을 경우
            applicationEventPublisher.publishEvent(new CommissionOpenCloseEvent(commissionCode, true));
        }
    }

    /** 관리자 예치금 withdraw 이후 해당 유저 예치금으로 transfer
     *
     * @param contract 환불을 진행할 계약
     */
    private void refund(Contract contract) {
        DepositHistoryInfo historyInfo = depositService.getDepositHistoryForRefund(
                contract.getInfo().clientCode(), contract.getCode());
        DepositProcessRequest adminWithdrawRequest = new DepositProcessRequest(adminMemberCode, contract.getCode(),
                Math.abs(historyInfo.changeAmount()), "환불 처리");

        depositService.withdraw(adminWithdrawRequest);

        DepositProcessRequest memberTransferRequest = new DepositProcessRequest(contract.getInfo().clientCode(), contract.getCode(),
                Math.abs(historyInfo.changeAmount()), "환불 처리");
        depositService.transfer(memberTransferRequest);
    }

    /** 해당 계약과 관련된 모든 정산 데이터를 하드 딜리트합니다.
     *
     * @param contract 정산 데이터를 지울 관련 계약
     */
    private void removeSettlements(Contract contract) {
        settlementService.deleteAllRelatedWith(contract);
    }

}
