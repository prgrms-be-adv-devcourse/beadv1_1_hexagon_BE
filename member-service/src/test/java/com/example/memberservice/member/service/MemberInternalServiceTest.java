package com.example.memberservice.member.service;


import com.example.memberservice.common.security.model.vo.Provider;
import com.example.memberservice.member.entity.Members;
import com.example.memberservice.member.entity.vo.Gender;
import com.example.memberservice.member.repository.MemberJpaRepository;
import com.example.memberservice.member.service.model.dto.output.MemberExistOutput;
import com.example.memberservice.member.service.model.dto.output.MemberInfoOutput;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@ActiveProfiles("test")
class MemberInternalServiceTest {

    @Autowired
    private MemberInternalService memberInternalService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("Member Code List를 받으면 존재하는 코드와 존재하지 않는 코드를 구분하여 반환한다")
    void getMemberExistsTest() {
        // Given
        String code1 = UUID.randomUUID().toString();
        String code2 = UUID.randomUUID().toString();
        String code3 = UUID.randomUUID().toString(); // DB에 없는 코드

        List<Members> members = List.of(
            createMember(code1, "AAA@gmail.com", "FirstUser"),
            createMember(code2, "BBB@gmail.com", "SecondUser")
        );

        memberJpaRepository.saveAll(members);

        List<String> inputCodes = List.of(code1, code2, code3);

        // When
        MemberExistOutput result = memberInternalService.getMemberExists(inputCodes);

        // Then
        System.out.println(result);
        assertThat(result.exists()).containsExactlyInAnyOrder(code1, code2);
        assertThat(result.notExists()).containsExactly(code3);
    }

    private Members createMember(String code, String email,String nickName) {
        return Members.builder()
            .code(code)
            .nickName(nickName)
            .email(email)
            .phoneNumber("010-9876-5432")
            .birthDate(LocalDate.of(1993, 5, 16))
            .gender(Gender.FEMALE)
            .provider(Provider.KAKAO)
            .providerId("kakao-67890")
            .build();
    }
}