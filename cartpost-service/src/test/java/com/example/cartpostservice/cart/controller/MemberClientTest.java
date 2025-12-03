package com.example.cartpostservice.cart.controller;

import com.example.cartpostservice.commissions.controller.dto.response.InternalMemberInfo;
import com.example.cartpostservice.commissions.controller.dto.response.MemberInfoOutput;
import com.example.cartpostservice.commissions.controller.internal.MemberClient;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hexagon.core.dto.ResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import org.springframework.test.context.ActiveProfiles;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getSuccessResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        // WireMock 서버 포트가 랜덤으로 뜨면, Feign이 그 포트를 바라보게 설정 덮어쓰기
        "spring.cloud.openfeign.client.config.member-service.url=http://localhost:${wiremock.server.port}"
})
@AutoConfigureWireMock(port = 0) // 0이면 랜덤 포트 사용
class MemberClientTest {

    @Autowired
    private MemberClient memberClient; // 실제 Feign Client 주입

    @Autowired
    private ObjectMapper objectMapper; // 응답 JSON 만들기용

    @Test
    @DisplayName("회원 코드로 조회 시 WireMock 서버와 통신하여 데이터를 가져온다")
    void getMemberInfoByCodeTest() throws Exception {
        // given
        String memberCode = "MEM_001";

        // 가짜 응답 데이터 생성
        MemberInfoOutput mockOutput = new MemberInfoOutput(List.of(
                new InternalMemberInfo("MEM_001", "TestUser", true)
        ));
        ResponseDto<MemberInfoOutput> mockResponse = getSuccessResponse(CustomStatusCode.SUCCESS, mockOutput);
        String jsonResponse = objectMapper.writeValueAsString(mockResponse);

        // WireMock 설정
        stubFor(get(urlPathEqualTo("/internal/members"))
                .withQueryParam("member-code", equalTo(memberCode)) // 파라미터 검증
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(jsonResponse)));

        // when
        // 실제 Feign Client 메서드 호출 (내부적으로 HTTP 요청이 WireMock으로 날아감)
        ResponseDto<MemberInfoOutput> result = memberClient.getMemberInfoByCode(List.of(memberCode));

        // then
        assertThat(result.data().internalMemberInfos()).hasSize(1);
        assertThat(result.data().internalMemberInfos().get(0).nickName()).isEqualTo("TestUser");

        // 실제로 WireMock에 요청이 1번 갔는지 검증
        verify(1, getRequestedFor(urlPathEqualTo("/internal/members")));
    }
}
