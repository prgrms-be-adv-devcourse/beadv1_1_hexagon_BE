package com.example.recommendationservice.client;

import com.example.recommendationservice.client.dto.output.CommissionReadOutput;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: 호출 대상 서비스 이름, path: 기본 경로
@FeignClient(
    name = "cartpost-service",
    path = "/internal/commissions"
)
public interface CommissionServiceClient {

    // 의뢰글 조회
    @GetMapping("/{commission-code}")
    ResponseDto<CommissionReadOutput> getCommission(
        @PathVariable("commission-code") String commissionCode
    );

}
