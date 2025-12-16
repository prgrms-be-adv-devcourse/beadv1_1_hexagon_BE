package com.example.profileservice.rating;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.profileservice.common.model.util.TestKafkaConfig;
import com.example.profileservice.common.model.vo.util.ContractFeignClient;
import com.example.profileservice.common.model.vo.util.ContractInfo;
import com.example.profileservice.common.model.vo.util.ContractStatus;
import com.example.profileservice.common.model.vo.util.MemberExistOutput;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
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
    private static final String VALID_CONTRACT_CODE = "contract-valid-code-12345";
    private static final String INVALID_CONTRACT_CODE = "contract-invalid-code-99999";
    private static final String UNCOMPLETED_CONTRACT_CODE = "contract-uncompleted-code-77777";
    private static final String ALREADY_RATED_CONTRACT_CODE = "contract-rated-code-88888";

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

    @MockitoBean
    private ContractFeignClient contractFeignClient;

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

        // 2-1. 유효한 계약 (DONE 상태, 당사자 매칭, 미평가)
        ContractInfo validContract = new ContractInfo(
                CALLER_CODE, // 클라이언트: CALLER_CODE
                RECEIVER_CODE_INITIALIZED, // 프리랜서: RECEIVER_CODE_INITIALIZED
                "comm-1",
                null, null, null, null,
                ContractStatus.DONE // 필수: DONE 상태
        );
        Mockito.when(contractFeignClient.getContractByCode(VALID_CONTRACT_CODE))
                .thenReturn(ResponseDto.success(validContract));

        // 2-2. 계약 당사자가 이미 평가 완료한 경우 (중복 평가 방지 테스트용)
        Mockito.when(contractFeignClient.isContractRatedBy(Mockito.eq(VALID_CONTRACT_CODE), Mockito.eq(CALLER_CODE)))
                .thenReturn(ResponseDto.success(false)); // 기본적으로 미평가 상태로 설정
        Mockito.when(contractFeignClient.isContractRatedBy(Mockito.eq(ALREADY_RATED_CONTRACT_CODE), Mockito.eq(CALLER_CODE)))
                .thenReturn(ResponseDto.success(true)); // 이미 평가 완료 상태로 설정

        // 2-3. 계약 상태가 DONE이 아닌 경우 (UNCOMPLETED_CONTRACT_CODE)
        ContractInfo uncompletedContract = validContract.progress(); // IN_PROGRESS로 상태 변경
        Mockito.when(contractFeignClient.getContractByCode(UNCOMPLETED_CONTRACT_CODE))
                .thenReturn(ResponseDto.success(uncompletedContract));

        // 2-4. 계약 코드가 존재하지 않는 경우 (INVALID_CONTRACT_CODE)
        Mockito.when(contractFeignClient.getContractByCode(INVALID_CONTRACT_CODE))
                .thenReturn(ResponseDto.success((ContractInfo) null)); // ContractInfo가 null인 성공 응답 가정

        // 3. RatingRequest 생성자에 contractCode 추가 (setUp에서는 Mocking된 VALID_CONTRACT_CODE 사용)
        RatingRequest satisfied = new RatingRequest(VALID_CONTRACT_CODE, true);
        RatingRequest unsatisfied = new RatingRequest(VALID_CONTRACT_CODE, false);

        // 만족 10회 증가
        for (int i = 0; i < 10; i++) {
            ratingService.updateRating(CALLER_CODE, RECEIVER_CODE_INITIALIZED, satisfied);
        }

        // 불만족 5회 증가
        for (int i = 0; i < 5; i++) {
            ratingService.updateRating(CALLER_CODE, RECEIVER_CODE_INITIALIZED, unsatisfied);
        }

        // 4. 요청 DTO 설정
        satisfiedRequest = new RatingRequest(VALID_CONTRACT_CODE, true);
        unsatisfiedRequest = new RatingRequest(VALID_CONTRACT_CODE, false);
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

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 계약 코드가 유효하지 않거나 찾을 수 없을 때 400 Bad Request")
    void updateRating_InvalidContractCode_Failure() throws Exception {
        // given: INVALID_CONTRACT_CODE 사용
        RatingRequest invalidContractRequest = new RatingRequest(INVALID_CONTRACT_CODE, true);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidContractRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3203)); // CONTRACT_NOT_FOUND
    }

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 계약 상태가 DONE이 아닐 때 400 Bad Request")
    void updateRating_ContractNotCompleted_Failure() throws Exception {
        // given: UNCOMPLETED_CONTRACT_CODE (IN_PROGRESS 상태로 Mocking됨) 사용
        RatingRequest uncompletedRequest = new RatingRequest(UNCOMPLETED_CONTRACT_CODE, true);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uncompletedRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3204)); // CONTRACT_NOT_COMPLETED
    }

    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 이미 평가 완료한 계약에 대해 재평가 시도 시 400 Bad Request")
    void updateRating_AlreadySubmitted_Failure() throws Exception {
        // given: ALREADY_RATED_CONTRACT_CODE 사용 (isContractRatedBy가 true를 반환하도록 Mocking됨)
        RatingRequest ratedRequest = new RatingRequest(ALREADY_RATED_CONTRACT_CODE, true);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratedRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3206)); // RATING_ALREADY_SUBMITTED
    }

    // 권한 관련 실패 테스트: CALLER_CODE가 계약 당사자가 아닌 경우 또는 이상한 회원을 평가하는 경우
    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - 평가 권한이 없을 때 (당사자 불일치) 400 Bad Request")
    void updateRating_UnauthorizedAccess_Failure() throws Exception {
        // given: 유효한 계약이지만, RECEIVER_CODE_NEW(계약 당사자가 아닌 자)를 평가하려고 시도
        // Mocking된 validContract: (CALLER_CODE <-> RECEIVER_CODE_INITIALIZED)

        // when & then: CALLER_CODE가 RECEIVER_CODE_NEW를 평가 시도 (계약 당사자 불일치)
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_NEW)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(satisfiedRequest))) // VALID_CONTRACT_CODE 포함
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3205)); // UNAUTHORIZED_RATING_ACCESS
    }


    @Test
    @DisplayName("PATCH /api/ratings/{memberCode} - RatingRequest의 contractCode 필드 누락 시 400 Bad Request")
    void updateRating_MissingContractCode_Failure() throws Exception {
        // given: contractCode 필드가 없는 요청
        String invalidRequestContent = "{\"satisfied\": true}";

        // when & then: 필수 필드 누락 검증
        mockMvc.perform(patch(BASE_URL + "/{memberCode}", RECEIVER_CODE_INITIALIZED)
                        .header(HEADER_X_CODE, CALLER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestContent))
                .andExpect(status().isBadRequest())
                // 계약 코드가 필수이므로, 해당 에러 메시지를 확인
                .andExpect(jsonPath("$.message").value("계약 코드는 필수입니다. (Field: contractCode)"))
                .andExpect(jsonPath("$.code").value(3001)); // INVALID_INPUT_VALUE
    }
}
