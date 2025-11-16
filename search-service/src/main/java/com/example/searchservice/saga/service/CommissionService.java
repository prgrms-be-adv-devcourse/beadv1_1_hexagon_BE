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
        commissionRepository.save(commission);
    }

    public void delete(String code) {
        commissionRepository.deleteById(code);
    }
}
