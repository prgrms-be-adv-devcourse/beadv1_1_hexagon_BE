package com.example.contractservice.settlement.service.mapper;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.domain.vo.SettlementReference;
import com.example.contractservice.settlement.domain.vo.SettlementStatusInfo;
import com.example.contractservice.settlement.domain.vo.SettlementTimeline;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.service.dto.request.SettlementSaveRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SettlementMapper {

    private static final int DAYS_PER_MONTH = 30;

    private SettlementMapper() {}

    public static Settlement toDomain(SettlementEntity settlementEntity) {
        SettlementReference reference = new SettlementReference(settlementEntity.getReceiverCode(),
                settlementEntity.getContractCode());

        SettlementStatusInfo statusInfo = new SettlementStatusInfo(settlementEntity.getOriginalAmount(),
                settlementEntity.getSettledAmount(), settlementEntity.getStatus(),
                settlementEntity.getSettlementRate());

        SettlementTimeline timeline = new SettlementTimeline(settlementEntity.getCreatedAt(),
                settlementEntity.getSettledAt(), settlementEntity.getProgressingAt());

        return new Settlement(settlementEntity.getId(), settlementEntity.getCode(), reference, statusInfo, timeline);
    }

    public static List<Settlement> toDomains(SettlementSaveRequest request) {
        SettlementReference reference = new SettlementReference(request.receiverCode(), request.contractCode());
        SettlementStatusInfo statusInfo = getStatusInfo(request.amount());

        return switch (request.paymentType()) {
            case PER_JOB -> getDomain(request, reference, statusInfo);
            case MONTHLY -> getDomains(request, reference);
        };
    }

    public static SettlementEntity toEntity(Settlement settlement) {
        SettlementReference reference = settlement.getSettlementReference();
        SettlementTimeline timeline = settlement.getSettlementTimeline();
        SettlementStatusInfo statusInfo = settlement.getSettlementStatusInfo();

        return SettlementEntity.builder()
                .id(settlement.getId())
                .code(settlement.getCode())
                .receiverCode(reference.receiverCode())
                .contractCode(reference.contractCode())
                .status(statusInfo.status())
                .originalAmount(statusInfo.originalAmount())
                .progressingAt(timeline.progressingAt())
                .createdAt(timeline.createdAt())
                .build();
    }

    public static void applyToEntity(Settlement settlement, SettlementEntity settlementEntity) {
        SettlementStatusInfo statusInfo = settlement.getSettlementStatusInfo();

        settlementEntity.updateInfo(statusInfo.settledAmount(),
                statusInfo.settlementRate(),
                settlement.getSettlementTimeline().settledAt(),
                statusInfo.status());
    }

    /** 단 건 타입인 경우, 프로젝트 종료일에 처리되는 정산 데이터가 생성됩니다.
     */
    private static List<Settlement> getDomain(SettlementSaveRequest request, SettlementReference reference,
            SettlementStatusInfo statusInfo) {

        SettlementTimeline timeline = getTimeline(request.endedAt());

        return Collections.singletonList(
                new Settlement(null, null, reference, statusInfo, timeline));
    }

    /** 월급 타입인 경우 프로젝트 시작일 기준 30일 단위로 나누어 금액을 계산, 도메인 처리합니다. 이 때, processingAt은 기준일 + 30일이 됩니다.
     * 남은 기간이 한 달보다 적거나 같다면 남은 금액 그대로 도메인으로 변환하고 processingAt은 마지막 기준일 + 30일이 됩니다.
     */
    private static List<Settlement> getDomains(SettlementSaveRequest request, SettlementReference reference) { // TODO: 계산 로직 수정 필요

        Long restAmount = request.amount(); // 남은 금액(최초에는 총 금액)
        Long monthAmount = getMonthAmount(request.amount(), request.startedAt(), request.endedAt()); // 30일 급여(월급)
        Instant curTime = request.startedAt(); // 기준 시각.

        List<Settlement> settlements = new ArrayList<>();

        while (curTime.isBefore(request.endedAt()) && restAmount > 0L) {
            curTime = curTime.plus(Duration.ofDays(DAYS_PER_MONTH));

            SettlementStatusInfo monthStatusInfo = getStatusInfo(monthAmount);

            SettlementTimeline timeline = getTimeline(curTime);

            settlements.add(new Settlement(null, null, reference, monthStatusInfo, timeline));

            restAmount = restAmount - monthAmount;
        } // 30일 단위로 끊어 저장

        if (restAmount > 0L) {
            settlements.add(new Settlement(null, null, reference, getStatusInfo(restAmount), getTimeline(curTime))); // 나머지 저장
        }

        return settlements;
    }

    private static SettlementTimeline getTimeline(Instant curTime) {
        return new SettlementTimeline(curTime);
    }

    /** 월급을 계산합니다.
     */
    private static Long getMonthAmount(Long amount, Instant startedAt, Instant endedAt) {
        long days = Duration.between(startedAt, endedAt).toDays();

        if (days < DAYS_PER_MONTH) { // 월급이 될 수 없다면 그대로 반환
            return amount;
        }

        return amount * DAYS_PER_MONTH / days;
    }

    private static SettlementStatusInfo getStatusInfo(Long amount) {
        return new SettlementStatusInfo(amount);
    }

}
