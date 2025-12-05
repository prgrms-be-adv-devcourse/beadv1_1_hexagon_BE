package com.example.contractservice.deposit.service;

import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.ALREADY_EXISTS;

import com.example.contractservice.common.aop.OptimisticRetry;
import com.example.contractservice.deposit.controller.dto.request.DepositRechargeRequest;
import com.example.contractservice.deposit.controller.dto.response.DepositHistoryCursorResponse;
import com.example.contractservice.deposit.controller.dto.response.DepositHistoryInfo;
import com.example.contractservice.deposit.controller.dto.response.DepositInfoResponse;
import com.example.contractservice.deposit.controller.dto.response.DepositRechargeResponse;
import com.example.contractservice.deposit.domain.Deposit;
import com.example.contractservice.deposit.domain.DepositHistory;
import com.example.contractservice.deposit.domain.exception.DepositException;
import com.example.contractservice.deposit.domain.vo.DepositChange;
import com.example.contractservice.deposit.repository.DepositRepository;
import com.example.contractservice.deposit.service.dto.request.DepositHistoryCursorRequest;
import com.example.contractservice.deposit.service.dto.request.DepositProcessRequest;
import com.example.contractservice.deposit.service.dto.response.DepositCreatedResponse;
import com.example.contractservice.deposit.service.dto.response.DepositProcessResponse;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {
    private static final int PAGE_SIZE = 20;

    private final DepositRepository depositRepository;

    public DepositInfoResponse getMyDeposit(String memberCode) {
        Deposit deposit = depositRepository.findDepositByMemberCode(memberCode);
        return DepositInfoResponse.of(deposit);
    }

    public DepositHistoryCursorResponse getDepositHistories(DepositHistoryCursorRequest request) {
        Deposit deposit = depositRepository.findDepositByMemberCode(request.memberCode());

        List<DepositHistory> contracts = depositRepository.findAllHistoriesBy(deposit.getCode(), request.cursorDate(),
                request.cursorCode(), PAGE_SIZE);

        return DepositHistoryCursorResponse.of(contracts, PAGE_SIZE);
    }

    public DepositHistoryInfo getDepositHistoryForRefund(String clientCode, String contractCode) {
        DepositHistory history = depositRepository.findHistoryBy(clientCode, contractCode);

        return new DepositHistoryInfo(history.getCreatedAt(),
                history.getDepositChange().changeAmount(),
                history.getDepositChange().resultAmount(),
                history.getSummary());
    }

    /** 회원가입한 사용자에 대한 예치금 엔티티를 생성합니다.
     *
     * @param memberCode 회원가입한 사용자의 코드
     * @return 생성된 예치금 정보
     */
    public DepositCreatedResponse createDeposit(String memberCode) {
        if (depositRepository.existMemberDeposit(memberCode)) {
            log.warn("member_code 값 {}에 해당하는 예치금 엔티티가 이미 존재합니다.", memberCode);

            throw new DepositException(ALREADY_EXISTS);
        }

        Deposit savedDeposit = depositRepository.saveDeposit(Deposit.createdBy(memberCode));

        return DepositCreatedResponse.of(savedDeposit.getCode());
    }

    @Transactional
    @OptimisticRetry
    public DepositRechargeResponse recharge(DepositRechargeRequest request) {
        DepositProcessRequest processRequest = new DepositProcessRequest(request.memberCode(), null, request.amount(),
                "예치금 입금");

        DepositProcessResponse processResponse = transfer(processRequest);

        return DepositRechargeResponse.of(processResponse.code(), processResponse.amount());
    }

    @Transactional
    @OptimisticRetry
    public DepositProcessResponse withdraw(DepositProcessRequest request) {
        Deposit deposit = process(request, Deposit::withdraw);

        return new DepositProcessResponse(deposit.getCode(), deposit.getMemberCode(),
                deposit.getAmount());
    }

    @Transactional
    @OptimisticRetry
    public DepositProcessResponse transfer(DepositProcessRequest request) {
        Deposit deposit = process(request, Deposit::transfer);

        return new DepositProcessResponse(deposit.getCode(), deposit.getMemberCode(),
                deposit.getAmount());
    }

    /**
     * 공통적인 메서드입니다. action은 예치금에 어떤 행동(출금/입금)을 하는지가 들어갑니다. </br>
     * 다음 순서로 처리됩니다. </br></br>
     * 1. 예치금을 DB에서 가져온다. </br>
     * 2. 예치금에 action(출금/입금 등) 한다. </br>
     * 3. 변동된 예치금을 바탕으로 예치금 내역을 만들고 저장한다. </br>
     */
    private Deposit process(DepositProcessRequest request, BiConsumer<Deposit, Long> action) {
        Deposit deposit = depositRepository.findDepositByMemberCode(request.memberCode());

        Long beforeAmount = deposit.getAmount();
        action.accept(deposit, request.amount());
        Long afterAmount = deposit.getAmount();

        depositRepository.saveDeposit(deposit);

        saveHistory(request, deposit, afterAmount - beforeAmount);
        return deposit;
    }

    private void saveHistory(DepositProcessRequest request, Deposit deposit, Long changeAmount) {
        DepositChange depositChange = new DepositChange(changeAmount, deposit.getAmount());
        DepositHistory depositHistory = new DepositHistory(deposit.getCode(), request.contractCode(), depositChange, request.summary());

        depositRepository.saveDepositHistory(depositHistory);
    }

}
