package com.example.memberservice.common.client;


import com.example.memberservice.common.client.dto.response.ContractStateResponse;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "contract-service",
    path = "/internal/contracts"
)
public interface ContractServiceClient {

    @GetMapping("{member-code}")
    ResponseDto<ContractStateResponse> existContractByRole(@PathVariable("member-code") String memberCode);

}
