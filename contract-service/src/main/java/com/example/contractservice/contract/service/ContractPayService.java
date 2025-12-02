package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.COMMISSION_RECRUIT_FULL;
import static com.example.contractservice.contract.domain.exception.ContractErrorCode.INVALID_PAYMENT_MEMBER;
import static com.example.contractservice.contract.domain.exception.ContractErrorCode.NOT_REQUESTED_STATUS;
import static com.example.contractservice.contract.service.mapper.ContractMapper.applyToEntity;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.CommissionsCapacityRepository;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.dto.request.ContractPayProcessRequest;
import com.example.contractservice.contract.service.mapper.ContractSettlementMapper;
import com.example.contractservice.deposit.service.DepositService;
import com.example.contractservice.deposit.service.dto.request.DepositProcessRequest;
import com.example.contractservice.settlement.service.SettlementService;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.contract.CommissionOpenCloseEvent;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractPayService {
    private static final String PAYMENT_COMMENT = "계약 결제";

    private final DepositService depositService;
    private final SettlementService settlementService;
    private final ContractRepository contractRepository;
    private final CommissionsCapacityRepository commissionsCapacityRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${admin.member.code}")
    private String adminMemberCode;

    @Transactional
    public void processPayment(ContractPayProcessRequest request) {
        Contract contract = request.contract();
        String xCode = request.xCode();

        validatePayment(xCode, contract);

        increaseSelectedCount(contract);

        transferToAdmin(xCode, contract);

        changeStatusToPay(contract, request.contractEntity());

        saveSettlements(contract);

        applicationEventPublisher.publishEvent(
                new ContractEvent(request.xCode(), contract.getCode(), contract.getCreatedAt(), ContractStatus.PAID.name()));
    }

    /**
     * 1. 로그인 사용자가 모든 계약과 연관되어 있는지 확인
     * 2. 클라이언트인지 확인
     */
    private void validatePayment(String xCode, Contract contract) {
        boolean isValidUser = contract.canUserPay(xCode); // 로그인 유저가 (계약에 관여) && 클라이언트

        if (!isValidUser) {
            throw new ContractException(INVALID_PAYMENT_MEMBER);
        }

        boolean isAnyNotRequested = !contract.isRequested() // REQUESTED 상태가 아니거나
                || contract.getInfo().startedAt().isBefore(Instant.now()); // REQUESTED인데 현재 시간보다 프로젝트 시작일이 이전이라면(실제로는 CANCELLED 상태)

        if (isAnyNotRequested) {
            throw new ContractException(NOT_REQUESTED_STATUS);
        }

    }

    private void increaseSelectedCount(Contract contract) {
        CommissionsCapacity capacity = commissionsCapacityRepository.findByCommissionCode(
                contract.getInfo().commissionCode());

        if (capacity.getSelectionCapacity() <= capacity.getSelectedCount()) {
            throw new ContractException(COMMISSION_RECRUIT_FULL);
        }

        capacity.increaseSelectedCount();

        if (capacity.getSelectionCapacity() == capacity.getSelectedCount()) {
            applicationEventPublisher.publishEvent(new CommissionOpenCloseEvent(capacity.getCommissionCode(), true)); // 의뢰글 자동 마감 처리
        }

        commissionsCapacityRepository.saveCapacity(capacity);
    }

    private void changeStatusToPay(Contract contract, ContractEntity entity) {
        contract.pay();

        applyToEntity(contract, entity);
        contractRepository.saveContract(entity);
    }

    /** 유저가 관리자 예치금으로 송금합니다. 월급/단건 타입에 따라 송금 금액이 결정되고 유저 예치금에서 빠져나가고 <br />
     * 관리자 예치금으로 입금됩니다.
     *
     * @param xCode 로그인 사용자 코드
     * @param contract 관련 계약
     */
    private void transferToAdmin(String xCode, Contract contract) {
        Long totalAmount = switch (contract.getInfo().paymentType()) {
            case MONTHLY -> {
                long projectDays = Duration.between(contract.getInfo().startedAt(), contract.getInfo().endedAt()).toDays();
                yield contract.getInfo().unitAmount() / 30 * projectDays;
            } // TODO: CORE 모듈에 따로 유틸 클래스 생성할 것
            case PER_JOB -> contract.getInfo().unitAmount();
        };

        DepositProcessRequest depositProcessRequest = new DepositProcessRequest(xCode, totalAmount, PAYMENT_COMMENT);
        depositService.withdraw(depositProcessRequest);

        DepositProcessRequest adminDepositProcessRequest = new DepositProcessRequest(adminMemberCode, totalAmount, "계약 결제 금액 수금");
        depositService.transfer(adminDepositProcessRequest); // 관리자 예치금으로 입금
    }

    private void saveSettlements(Contract contract) {
        settlementService.savePaidSettlements(ContractSettlementMapper.toSaveRequest(contract));
    }
}
