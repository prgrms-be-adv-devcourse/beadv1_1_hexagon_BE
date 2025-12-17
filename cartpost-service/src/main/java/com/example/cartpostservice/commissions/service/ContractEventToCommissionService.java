package com.example.cartpostservice.commissions.service;

import com.example.cartpostservice.commissions.model.CommissionsEntity;
import com.example.cartpostservice.commissions.repository.CommissionsRepository;
import com.example.cartpostservice.common.exception.BusinessException;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractEventToCommissionService {

    private final CommissionsRepository contractsRepository;

    public void updateToSyncData(String commissionCode, String status) {
        CommissionsEntity commission = contractsRepository.findByCode(commissionCode)
                .orElseThrow(() -> new BusinessException(CustomStatusCode.BAD_REQUEST_COMMISSION));

        if (status.equals("REQUESTED") || status.equals("PAID") || status.equals("CANCELLED")) {
            commission.updateLastSyncTime(null);
        }
    }
}
