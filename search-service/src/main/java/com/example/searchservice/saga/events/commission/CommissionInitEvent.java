package com.example.searchservice.saga.events.commission;

import com.example.searchservice.commission.service.dto.CartPostCommissionDto;
import java.util.List;

public record CommissionInitEvent(
        List<CartPostCommissionDto> commissions
) {

}
