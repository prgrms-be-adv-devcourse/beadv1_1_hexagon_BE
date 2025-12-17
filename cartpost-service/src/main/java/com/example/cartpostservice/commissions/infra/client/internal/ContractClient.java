package com.example.cartpostservice.commissions.infra.client.internal;

import com.example.cartpostservice.commissions.infra.client.internal.dto.request.TotalPeopleInfoRequestDto;
import com.example.cartpostservice.commissions.infra.client.internal.dto.response.PeopleInfoResponseDto;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "contract-service", path = "/internal/contracts/commissions-capacity", contextId = "applicantClient")

public interface ContractClient {

    @GetMapping("/{commission-code}")
    ResponseDto<PeopleInfoResponseDto> getNumberOfPeople(@PathVariable(name = "commission-code") String commissionCode);

    @PostMapping()
    ResponseDto<Empty> upsertNumberOfPeople(@RequestBody TotalPeopleInfoRequestDto totalPeopleInfoRequestDto);
}
