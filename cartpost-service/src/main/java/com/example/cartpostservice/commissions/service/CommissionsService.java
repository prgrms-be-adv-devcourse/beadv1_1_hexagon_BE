package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.model.vo.RecruitmentStatus;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.commissions.service.dto.request.CommissionsServiceCommand;
import com.example.cartpostservice.commissions.service.dto.response.CommissionsServiceResult;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommissionsService implements CrudService<CommissionsServiceCommand, CommissionsServiceResult, String> {

    private final CommissionsRepository commissionsRepository;

    @Override
    public String create(CommissionsServiceCommand requestDto) {
        CommissionsEntity commissions = CommissionsEntity.builder()
                .memberCode(requestDto.memberCode())
                .title(requestDto.title())
                .content(requestDto.content())
                .paymentType(requestDto.paymentType())
                .unitAmount(requestDto.unitAmount())
                .startedAt(requestDto.startedAt())
                .endedAt(requestDto.endedAt())
                .writerName(requestDto.writerName())
                .recruitmentStatus(RecruitmentStatus.OPEN)
                .build();

        CommissionsEntity saved = commissionsRepository.save(commissions);

        return saved.getCode();
    }

    @Override
    public CommissionsServiceResult read(String commissionsCode) {

        CommissionsEntity commission = commissionsRepository.findByCode(commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.NOT_FOUND_COMMISSION));

        return new CommissionsServiceResult(
                commission.getCode(),
                commission.getMemberCode(),
                commission.getTitle(),
                commission.getContent(),
                commission.getPaymentType(),
                commission.getUnitAmount(),
                commission.getStartedAt(),
                commission.getEndedAt(),
                commission.getRecruitmentStatus(),
                commission.getWriterName(),
                commission.getUpdatedAt()
        );
    }

    @Override
    @Transactional
    public void update(CommissionsServiceCommand commissionsServiceCommand, String commissionsCode) {
        List<CommissionsEntity> commissions = commissionsRepository.findByMemberCode(
                commissionsServiceCommand.memberCode());
        if (commissions.isEmpty()) {
            throw new BusinessException(CustomStatusCode.NOT_FOUND_COMMISSION);
        }

        CommissionsEntity foundEntity = commissions.stream()
                .filter(entity -> entity.getCode().equals(commissionsCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        foundEntity.update(
                commissionsServiceCommand.memberCode(),
                commissionsServiceCommand.title(),
                commissionsServiceCommand.content(),
                commissionsServiceCommand.paymentType(),
                commissionsServiceCommand.unitAmount(),
                commissionsServiceCommand.startedAt(),
                commissionsServiceCommand.endedAt(),
                commissionsServiceCommand.writerName()
        );

        // 자동 반영
        //CommissionsEntity saved = commissionsRepository.save(foundEntity);
    }

    @Override
    public void delete(String memberCode, String commissionsCode) {
        CommissionsEntity commission = commissionsRepository.findByMemberCodeAndCode(memberCode, commissionsCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commissionsRepository.delete(commission);
    }

    public Page<CommissionsServiceResult> getPage(String memberCode, Pageable pageable) {

        Page<CommissionsEntity> commissions = commissionsRepository.findPageByMemberCode(memberCode, pageable);

        return commissions.map(commission ->
                new CommissionsServiceResult(
                        commission.getCode(),
                        commission.getMemberCode(),
                        commission.getTitle(),
                        commission.getContent(),
                        commission.getPaymentType(),
                        commission.getUnitAmount(),
                        commission.getStartedAt(),
                        commission.getEndedAt(),
                        commission.getRecruitmentStatus(),
                        commission.getWriterName(),
                        commission.getUpdatedAt()
                )
        );
    }

    public boolean isOwner(String memberCode, String commissionsCode) {
        return commissionsRepository
                .findByMemberCodeAndCode(memberCode, commissionsCode)
                .isPresent();
    }

    public void closeCommission(String commissionCode) {
        CommissionsEntity commission = commissionsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.FORBIDDEN_COMMISSION));

        commission.closeRecruitmentStatus();
    }
}