package com.example.searchservice.saga.service;

import com.example.searchservice.commission.entity.CommissionDocumentEntity;
import com.example.searchservice.commission.repository.CommissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionRepository commissionRepository;

    public void saveAll(List<CommissionDocumentEntity> commissions) {
        commissionRepository.saveAll(commissions);
    }

    public void save(CommissionDocumentEntity commission) {
        commissionRepository.save(commission);
    }

    public void update(CommissionDocumentEntity commission) {
        commissionRepository.findById(commission.getCode())
                .ifPresentOrElse(existing -> {
                    existing.setCode(commission.getCode());
                    existing.setTitle(commission.getTitle());
                    existing.setContent(commission.getContent());
                    existing.setMemberCode(commission.getMemberCode());
                    existing.setMemberNickname(commission.getMemberNickname());
                    existing.setTags(commission.getTags());
                    existing.setStartedAt(commission.getStartedAt());
                    existing.setEndedAt(commission.getEndedAt());
                    existing.setPaymentType(commission.getPaymentType());
                    existing.setPayAmount(commission.getPayAmount());
                    existing.setIsClosed(commission.getIsClosed());
                    existing.setUpdatedAt(commission.getUpdatedAt());

                    commissionRepository.save(existing);
                }, () -> {
                    commissionRepository.save(commission);
                });
    }

    public void delete(String code) {
        commissionRepository.deleteById(code);
    }
}
