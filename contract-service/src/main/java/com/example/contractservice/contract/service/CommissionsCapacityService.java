package com.example.contractservice.contract.service;

import com.example.contractservice.contract.controller.dto.request.CommissionsCapacityUpsertRequest;
import com.example.contractservice.contract.controller.dto.response.CommissionCapacityResponse;
import com.example.contractservice.contract.entity.CommissionsCapacity;
import com.example.contractservice.contract.repository.CommissionsCapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionsCapacityService {
    private final CommissionsCapacityRepository commissionsCapacityRepository;

    public void upsertCapacity(CommissionsCapacityUpsertRequest request) {
        CommissionsCapacity commissionsCapacity = commissionsCapacityRepository.findByCommissionCodeOrCreate(
                request.commissionCode(), request.applyCapacity(), request.selectionCapacity());

        commissionsCapacity.updateInfo(request.applyCapacity(), request.selectionCapacity());

        commissionsCapacityRepository.saveCapacity(commissionsCapacity);
    }

    public CommissionCapacityResponse getCapacity(String commissionCode) {
        CommissionsCapacity capacity = commissionsCapacityRepository.findByCommissionCode(commissionCode);

        return CommissionCapacityResponse.from(capacity);
    }
}
