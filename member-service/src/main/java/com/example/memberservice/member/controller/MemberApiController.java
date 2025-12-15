package com.example.memberservice.member.controller;


import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.member.controller.dto.request.MemberRoleUpdateRequest;
import com.example.memberservice.member.model.enums.MemberRole;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import com.example.memberservice.member.controller.dto.request.MemberCreateRequest;
import com.example.memberservice.member.controller.dto.request.MemberUpdateRequest;
import com.example.memberservice.member.controller.dto.response.MemberGetResponse;
import com.example.memberservice.member.controller.swagger.MemberApiControllerSwagger;
import com.example.memberservice.member.mapper.MemberServiceInputMapper;
import com.example.memberservice.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController implements MemberApiControllerSwagger {

    //api 명세
    // POST /
    // PATCH /
    // DELETE /
    // GET /?member-code
    // GET /check-name?name

    private final MemberService memberService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<MemberGetResponse> getMemberByCode(
        @RequestParam(name = "member-code") String paramCode
    ) {
        return ResponseDto.success(
            memberService.getMemberByCode(MemberServiceInputMapper.toGetMemberInput(paramCode)));
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<MemberGetResponse> getMyMemberByCode(
        @RequestHeader(name = "X-CODE") String xCode) {
        return ResponseDto.success(
            memberService.getMemberByCode(MemberServiceInputMapper.toGetMemberInput(xCode)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<Empty> createMember(@RequestHeader(name = "X-CODE") String memberCode,
        @RequestBody MemberCreateRequest request) {

        memberService.createMember(
            MemberServiceInputMapper.toCreateMemberInput(memberCode, request));

        return ResponseDto.success(HttpStatus.CREATED);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> updateMember(@RequestHeader(name = "X-CODE") String memberCode,
        @RequestBody MemberUpdateRequest request) {

        memberService.updateMember(
            MemberServiceInputMapper.toUpdateMemberInput(memberCode, request));
        return ResponseDto.success();
    }

    @PatchMapping("/state")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> updateMemberRoleState(@RequestHeader("X-CODE") String memberCode,
        @RequestBody MemberRoleUpdateRequest request) {

        if (!request.role().equals(MemberRole.CLIENT)) {
            throw new BusinessException(ErrorCode.NOT_ALLOW_ROLE_UPDATE);
        }

        memberService.updateMemberRoleState(
            MemberServiceInputMapper.toUpdateMemberRoleStateInput(memberCode, request));
        return ResponseDto.success();
    }

    @DeleteMapping("/state")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> deleteMemberRoleState(@RequestHeader("X-CODE") String memberCode,
        @RequestBody MemberRoleUpdateRequest request) {

        memberService.deleteMemberRoleState(
            MemberServiceInputMapper.toUpdateMemberRoleStateInput(memberCode, request));

        return ResponseDto.success();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> deleteMember(@RequestHeader("X-CODE") String memberCode
    ) {

        memberService.deleteMember(MemberServiceInputMapper.toDeleteMemberInput(memberCode));

        return ResponseDto.success();
    }

    @GetMapping("/check-name")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> existMemberByName(@RequestHeader("X-CODE") String memberCode,
        @RequestParam(name = "name") String name) {

        memberService.existMemberByNickName(
            MemberServiceInputMapper.toExistMemberByNameInput(memberCode, name));

        return ResponseDto.success();
    }
}
