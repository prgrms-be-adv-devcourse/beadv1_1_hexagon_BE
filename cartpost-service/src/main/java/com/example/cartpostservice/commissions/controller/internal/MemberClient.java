package com.example.cartpostservice.commissions.controller.internal;

import com.example.cartpostservice.commissions.controller.dto.response.MemberInfoOutput;
import com.example.cartpostservice.common.dto.ResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-service")
public interface MemberClient {

    @GetMapping("/internal/members")
    ResponseDto<MemberInfoOutput> getMemberInfoByCode(@RequestParam("member-code") List<String> memberCodes);
}
