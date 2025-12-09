package com.example.memberservice.member.service;


import com.example.memberservice.member.controller.dto.response.MemberGetResponse;
import com.example.memberservice.member.service.model.dto.input.MemberCreateInput;
import com.example.memberservice.member.service.model.dto.input.MemberDeleteInput;
import com.example.memberservice.member.service.model.dto.input.MemberExistByNameInput;
import com.example.memberservice.member.service.model.dto.input.MemberGetInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateWorkStateInput;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    /**
     * 마이페이지 회원 조회 (X-CODE 또는 member-code 기준)
     */
    MemberGetResponse getMemberByCode(MemberGetInput input);

    /**
     * 회원 생성
     */
    void createMember(MemberCreateInput input);

    /**
     * 회원 정보 수정
     */
    void updateMember(MemberUpdateInput input);

    /**
     * 회원 판매자 등록 상태 true로 변경
     */
    void updateMemberRoleState(MemberUpdateWorkStateInput input);

    /**
     * 회원 판매자 등록 상태 true로 변경
     */
    void deleteMemberRoleState(MemberUpdateWorkStateInput input);


    /**
     * 회원 삭제
     */
    void deleteMember(MemberDeleteInput input);

    /**
     * 닉네임 중복 확인
     */
    void existMemberByNickName(MemberExistByNameInput input);
}
