package com.example.profileservice.rating;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.profileservice.common.model.vo.util.MemberExistOutput;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
import com.example.profileservice.common.model.util.TestKafkaConfig;
import com.example.profileservice.rating.model.dto.request.RatingRequest;
import com.example.profileservice.rating.repository.RatingRepository;
import com.example.profileservice.rating.service.RatingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.hexagon.core.dto.ResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestKafkaConfig.class)
public class RatingControllerTest {

    private static final String BASE_URL = "/api/ratings";
    private static final String HEADER_X_CODE = "X-CODE";

    private static final String CALLER_CODE = "member-caller-uuid-001";
    private static final String RECEIVER_CODE_INITIALIZED = "member-receiver-uuid-010";
    private static final String RECEIVER_CODE_NEW = "member-receiver-uuid-020";
    private static final String RECEIVER_CODE_INVALID = "invalid-member-code-999";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RatingService ratingService;

    @MockitoBean
    private MemberFeignClient memberFeignClient;

    private RatingRequest satisfiedRequest;
    private RatingRequest unsatisfiedRequest;

    @BeforeEach
    void setUp() {
        // Mocking 설정: 모든 유효한 요청에 대해 성공 응답 반환
        List<String> validCodes = List.of(CALLER_CODE, RECEIVER_CODE_INITIALIZED, RECEIVER_CODE_NEW);

        // Mock 응답 생성: 모든 코드가 exists 리스트에 포함되어야 함
        MemberExistOutput mockExistOutput = new MemberExistOutput(validCodes, List.of());
        ResponseDto<MemberExistOutput> mockSuccessResponse = ResponseDto.success(mockExistOutput);

        // memberServiceClient.existMemberByCode 호출 시 성공 응답 반환하도록 Mocking
        Mockito.when(memberFeignClient.existMemberByCode(Mockito.anyList()))
                .thenReturn(mockSuccessResponse);

        // 1. 초기 평가 데이터 설정 및 저장 (만족 10, 불만족 5)
        RatingRequest satisfied = new RatingRequest(true);
        RatingRequest unsatisfied = new RatingRequest(false);

        // Note: updateRating 호출 시, Feign Client Mocking이 작동하여 유효성 검증을 통과해야 함.
        // 만족 10회 증가
        for (int i = 0; i < 10; i++) {
            ratingService.updateRating(CALLER_CODE, RECEIVER_CODE_INITIALIZED, satisfied);
        }

        // 불만족 5회 증가
        for (int i = 0; i < 5; i++) {
            ratingService.updateRating(CALLER_CODE, RECEIVER_CODE_INITIALIZED, unsatisfied);
        }

        // 2. 요청 DTO 설정
        satisfiedRequest = new RatingRequest(true);
        unsatisfiedRequest = new RatingRequest(false);
    }

    // 평가 조회 (GET) 테스트

    @Test
    @DisplayName("GET /api/ratings/{memberCode} - 기존 평가가 있는 회원의 평가 조회 성공")
    void getMemberRating_Existing_Success() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberCode").value(RECEIVER_CODE_INITIALIZED))
                .andExpect(jsonPath("$.data.satisfiedCount").value(10))
                .andExpect(jsonPath("$.data.unsatisfiedCount").value(5));
    }

    @Test
    @DisplayName("GET /api/ratings/{memberCode} - 평가가 없는 회원의 평가 조회 성공 (0/0 반환)")
    void getMemberRating_NonExisting_Success() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/{memberCode}", RECEIVER_CODE_NEW))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberCode").value(RECEIVER_CODE_NEW))
                .andExpect(jsonPath("$.data.satisfiedCount").value(0))
                .andExpect(jsonPath("$.data.unsatisfiedCount").value(0));
    }

    // 평가 등록/업데이트 (PATCH) 테스트

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 기존 회원에게 만족 평가 업데이트 성공")
    void updateRating_Satisfied_Existing_Success() throws Exception {
        // given: 초기 만족 10
        // when & then: 만족 평가 1회 추가
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(satisfiedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.satisfiedCount").value(11)) // 10 -> 11
                .andExpect(jsonPath("$.data.unsatisfiedCount").value(5)); // 불만족 유지
    }

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 기존 회원에게 불만족 평가 업데이트 성공")
    void updateRating_Unsatisfied_Existing_Success() throws Exception {
        // given: 초기 불만족 5
        // when & then: 불만족 평가 1회 추가
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unsatisfiedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.satisfiedCount").value(10)) // 만족 유지
                .andExpect(jsonPath("$.data.unsatisfiedCount").value(6)); // 5 -> 6
    }

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 평가 엔티티가 없는 회원에게 최초 만족 평가 성공 (엔티티 생성)")
    void updateRating_Satisfied_New_Success() throws Exception {
        // when & then
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_NEW)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(satisfiedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberCode").value(RECEIVER_CODE_NEW))
                .andExpect(jsonPath("$.data.satisfiedCount").value(1)) // 최초 1
                .andExpect(jsonPath("$.data.unsatisfiedCount").value(0));
    }

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 자기 자신 평가 시도 시 400 Bad Request")
    void updateRating_SelfRating_Failure() throws Exception {
        // when & then: CALLER_CODE가 CALLER_CODE를 평가 시도
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", CALLER_CODE)
                        .header(HEADER_X_CODE, CALLER_CODE) // 동일한 코드 사용
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(satisfiedRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3202)); // CANNOT_RATE_MYSELF
    }

    // 추가된 유효성 검증 실패 테스트
    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 유효하지 않은 회원 코드로 평가 시도 시 400 Bad Request")
    void updateRating_InvalidMemberCode_Failure() throws Exception {
        // given: 유효하지 않은 회원 코드를 시뮬레이션하기 위한 Mocking 재설정
        // Mockito.reset(memberServiceClient); // 기존 Mocking 리셋 (필요시)

        List<String> invalidCodes = List.of(CALLER_CODE, RECEIVER_CODE_INVALID);

        // CALLER_CODE만 존재하고 RECEIVER_CODE_INVALID는 존재하지 않도록 응답 설정
        MemberExistOutput mockExistOutputFailure = new MemberExistOutput(
                List.of(CALLER_CODE), // 존재하는 코드
                List.of(RECEIVER_CODE_INVALID) // 존재하지 않는 코드
        );
        ResponseDto<MemberExistOutput> mockFailureResponse = ResponseDto.success(mockExistOutputFailure);

        // 특정 인수를 받았을 때 실패 응답을 반환하도록 설정
        Mockito.when(memberFeignClient.existMemberByCode(invalidCodes))
                .thenReturn(mockFailureResponse);

        // when & then: 유효하지 않은 회원 코드로 평가 시도
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INVALID)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(satisfiedRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3006)); // INVALID_MEMBER_CODE
    }

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - RatingRequest의 satisfied 필드 누락 시 400 Bad Request")
    void updateRating_MissingRequiredField_Failure() throws Exception {
        // given: satisfied 필드가 없는 요청
        String invalidRequestContent = "{}";

        // when & then: 필수 필드 누락 검증
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("만족 여부는 필수입니다. (Field: satisfied)"))
                .andExpect(jsonPath("$.message").exists());
    }
}
