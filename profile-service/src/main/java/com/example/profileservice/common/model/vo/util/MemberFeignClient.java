package com.example.profileservice.common.model.vo.util;

import com.example.memberservice.member.service.model.dto.output.MemberExistOutput;
import com.example.memberservice.member.service.model.dto.output.MemberInfoOutput;
import com.example.profileservice.common.model.vo.ResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// member-service의 /internal/members 경로로 요청
@FeignClient(name = "MEMBER-SERVICE", url = "http://MEMBER-SERVICE/internal/members")
public interface MemberFeignClient {

    @GetMapping("/exist")
    ResponseDto<MemberExistOutput> existMemberByCode(
            @RequestParam(name = "member-code") List<String> memberCodes);

    @GetMapping
    ResponseDto<MemberInfoOutput> getMemberInfoByCode(
            @RequestParam(name = "member-code") List<String> memberCodes);
}
