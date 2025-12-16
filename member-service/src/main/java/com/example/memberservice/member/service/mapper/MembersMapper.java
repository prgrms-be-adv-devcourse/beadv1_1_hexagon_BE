package com.example.memberservice.member.service.mapper;

import com.example.memberservice.member.model.entity.Members;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.vo.ApiMemberInfo;

public class MembersMapper {

    public static ApiMemberInfo toGetDto(Members members) {
        return new ApiMemberInfo(
            members.getCode(),
            members.getNickName(),
            members.getEmail(),
            members.getPhoneNumber(),
            members.getBirthDate().toString(),
            members.getGender().name(),
            members.getRole()
        );
    }

    public static void toApply(Members members, MemberUpdateInput memberUpdateInput){
        members.updateBirthDate(memberUpdateInput.birthDate());
        members.updateGender(memberUpdateInput.gender());
        members.updateNickName(memberUpdateInput.name());
        members.updatePhoneNumber(memberUpdateInput.phoneNumber());
    }

}
