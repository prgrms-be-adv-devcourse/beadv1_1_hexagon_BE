package com.example.memberservice.member.controller;

import org.hexagon.core.dto.ResponseDto;
import com.example.memberservice.member.controller.swagger.MemberInternalControllerSwagger;
import com.example.memberservice.member.service.MemberInternalService;
import com.example.memberservice.member.service.model.dto.output.MemberExistOutput;
import com.example.memberservice.member.service.model.dto.output.MemberInfoOutput;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/members")
@RequiredArgsConstructor
public class MemberInternalController implements MemberInternalControllerSwagger {

    // GET /?member-code
    // GET /exist?member-code

    private final MemberInternalService memberInternalService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<MemberInfoOutput> getMemberInfoByCode(
        @RequestParam(name = "member-code", required = false) List<String> paramMemberCode) {

        return ResponseDto.success(memberInternalService.getMemberInfos(paramMemberCode));
    }

    @GetMapping("/exist")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<MemberExistOutput> existMemberByCode(
        @RequestParam(name = "member-code", required = false) List<String> paramMemberCode) {

        return ResponseDto.success(memberInternalService.getMemberExists(paramMemberCode));
    }
}
