package com.example.contractservice.deposit.service;

import static com.example.contractservice.deposit.domain.exception.DepositErrorCode.ALREADY_EXISTS;
import static com.example.contractservice.deposit.service.mapper.DepositHistoryMapper.toEntity;
import static com.example.contractservice.deposit.service.mapper.DepositMapper.toDomain;

import com.example.contractservice.deposit.controller.dto.request.DepositRechargeRequest;
import com.example.contractservice.deposit.controller.dto.response.DepositRechargeResponse;
import com.example.contractservice.deposit.domain.Deposit;
import com.example.contractservice.deposit.domain.DepositHistory;
import com.example.contractservice.deposit.domain.exception.DepositException;
import com.example.contractservice.deposit.entity.DepositEntity;
import com.example.contractservice.deposit.entity.DepositHistoryEntity;
import com.example.contractservice.deposit.repository.DepositRepository;
import com.example.contractservice.deposit.service.dto.request.DepositProcessRequest;
import com.example.contractservice.deposit.service.dto.response.DepositCreatedResponse;
import com.example.contractservice.deposit.service.dto.response.DepositProcessResponse;
import com.example.contractservice.deposit.service.mapper.DepositHistoryMapper;
import com.example.contractservice.deposit.service.mapper.DepositMapper;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {
    private final DepositRepository depositRepository;

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

        DepositEntity savedDeposit = depositRepository.saveDeposit(DepositEntity.createBy(memberCode));

        return DepositCreatedResponse.of(savedDeposit.getCode());
    }

    @Transactional
    public DepositRechargeResponse recharge(DepositRechargeRequest request) { // TODO: 동시성 테스트
        DepositProcessRequest processRequest = new DepositProcessRequest(request.memberCode(), request.amount(),
                "예치금 입금");

        DepositProcessResponse processResponse = transfer(processRequest);

        return DepositRechargeResponse.of(processResponse.code(), processResponse.amount());
    }

    @Transactional
    public DepositProcessResponse withdraw(DepositProcessRequest request) {
        DepositEntity depositEntity = process(request, Deposit::withdraw);

        return new DepositProcessResponse(depositEntity.getCode(), depositEntity.getMemberCode(),
                depositEntity.getAmount());
    }

    @Transactional
    public DepositProcessResponse transfer(DepositProcessRequest request) {
        DepositEntity depositEntity = process(request, Deposit::transfer);

        return new DepositProcessResponse(depositEntity.getCode(), depositEntity.getMemberCode(),
                depositEntity.getAmount());
    }

    /**
     * 공통적인 메서드입니다. action은 예치금에 어떤 행동(출금/입금)을 하는지가 들어갑니다. </br>
     * 다음 순서로 처리됩니다. </br></br>
     * 1. 예치금을 DB에서 가져온다. </br>
     * 2. 예치금에 action(출금/입금 등) 한다. </br>
     * 3. 변동된 예치금을 바탕으로 예치금 내역을 만들고 저장한다. </br>
     */
    private DepositEntity process(DepositProcessRequest request, BiConsumer<Deposit, Long> action) {
        DepositEntity depositEntity = depositRepository.findDepositByMemberCode(request.memberCode());
        Deposit deposit = toDomain(depositEntity);

        Long beforeAmount = deposit.getAmount();
        action.accept(deposit, request.amount());
        Long afterAmount = deposit.getAmount();

        DepositMapper.applyToEntity(deposit, depositEntity);
        depositRepository.saveDeposit(depositEntity);

        saveHistory(request, deposit, afterAmount - beforeAmount);
        return depositEntity;
    }

    private void saveHistory(DepositProcessRequest request, Deposit deposit, Long changeAmount) {
        DepositHistory depositHistory = DepositHistoryMapper.toDomain(deposit, changeAmount, request.summary());
        DepositHistoryEntity depositHistoryEntity = toEntity(depositHistory);

        depositRepository.saveDepositHistory(depositHistoryEntity);
    }

}
