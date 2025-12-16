package com.example.memberservice.member.service;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.member.model.entity.Members;
import com.example.memberservice.member.repository.MemberJpaRepository;
import com.example.memberservice.member.service.model.dto.output.MemberExistOutput;
import com.example.memberservice.member.service.model.dto.output.MemberInfoOutput;
import com.example.memberservice.member.service.model.vo.InternalMemberInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberInternalService {

    private final MemberJpaRepository memberJpaRepository;

    public MemberInfoOutput getMemberInfos(List<String> memberCodes) {
        Set<String> setMemberCodes = new HashSet<>(memberCodes);

        List<Members> findMembers = memberJpaRepository.findAllByCodeInAndIsDeletedFalse(setMemberCodes);

        if (setMemberCodes.size() > findMembers.size()) {
            throw new BusinessException(ErrorCode.INTERNAL_ILLEGAL_MEMBER_CODE);
        }

        return new MemberInfoOutput(
            findMembers.stream()
                .map(m -> new InternalMemberInfo(
                    m.getCode(),
                    m.getNickName(),
                    m.getRole()
                ))
                .toList()
        );
    }

    public MemberExistOutput getMemberExists(List<String> memberCodes) {
        Set<String> setMemberCodes = new HashSet<>(memberCodes);

        List<Members> findMembers = memberJpaRepository.findAllByCodeInAndIsDeletedFalse(setMemberCodes);

        Set<String> existCodes = findMembers.stream()
            .map(Members::getCode)
            .collect(Collectors.toSet());

        List<String> exists = memberCodes.stream()
            .filter(existCodes::contains)
            .toList();

        List<String> notExists = memberCodes.stream()
            .filter(code -> !existCodes.contains(code))
            .toList();

        return new MemberExistOutput(exists, notExists);
    }
}
