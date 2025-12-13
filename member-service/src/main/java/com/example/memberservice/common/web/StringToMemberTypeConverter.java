package com.example.memberservice.common.web;

import com.example.memberservice.member.model.enums.MemberRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToMemberTypeConverter implements Converter<String, MemberRole> {
    @Override
    public MemberRole convert(String source) {
        return MemberRole.valueOf(source.toUpperCase());
    }
}