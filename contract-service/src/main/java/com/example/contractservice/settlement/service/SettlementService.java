package com.example.contractservice.settlement.service;

import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.repository.SettlementRepository;
import com.example.contractservice.settlement.service.dto.request.SettlementSaveRequest;
import com.example.contractservice.settlement.service.dto.response.SettlementSaveResponse;
import com.example.contractservice.settlement.service.mapper.SettlementMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {
    private final SettlementRepository settlementRepository;

    /** 정산 데이터를 저장합니다. 월급은 프로젝트 시작일 기준 30일 단위로 정산 데이터가 1개씩 저장되고, </br>
     * 건당은 프로젝트 종료일 기준으로 데이터가 1개만 저장됩니다. 실제 정산 처리는 주기적인 배치로 이루어집니다.
     *
     * @param request 정산 저장을 위해 필요한 DTO. 프리랜서 코드, 금액 정보 등을 포함
     * @return 정산 엔티티 저장 데이터들에 대한 DTO
     */
    @Transactional
    public List<SettlementSaveResponse> savePaidSettlements(SettlementSaveRequest request) {
        return processByPaymentType(request) // 정산 저장
                .stream()
                .map(SettlementSaveResponse::from) // 처리된 정산 목록을 DTO 목록으로 변환
                .toList();
    }

    private List<Settlement> processByPaymentType(SettlementSaveRequest request) {
        List<Settlement> settlements = SettlementMapper.toDomains(request);
        settlements.stream()
                .map(SettlementMapper::toEntity)
                .forEach(settlementRepository::save); // TODO: 레포지토리 인자로 도메인을 받도록 수정

        return settlements;
    }
}
