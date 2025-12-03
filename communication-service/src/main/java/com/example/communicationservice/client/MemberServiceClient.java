package com.example.communicationservice.client;

import com.example.communicationservice.client.dto.MemberExistOutput;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// name: 호출 대상 서비스 이름, path: 기본 경로
@FeignClient(
    name = "member-service",
    path = "/internal/members"
)
public interface MemberServiceClient {

    @GetMapping("/exist")
    ResponseDto<MemberExistOutput> getMemberExistences(
        @RequestParam(name = "member-code") List<String> memberCodes
    );

}
