package com.example.contractservice.common.util.feign;

import org.hexagon.core.dto.ResponseDto;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service", path = "/internal/members")
public interface MemberClient {
    @GetMapping
    ResponseDto<MemberInfoResponse> getMemberInfo(@RequestParam("member-code") List<String> memberCodes);
}
