package com.example.profileservice.resume;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.memberservice.member.service.model.dto.output.MemberExistOutput;
import com.example.profileservice.common.model.vo.ResponseDto;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
import com.example.profileservice.common.model.vo.util.TestKafkaConfig;
import com.example.profileservice.experience.model.dto.request.ExperienceRequest;
import com.example.profileservice.experience.model.entity.ExperienceEntity;
import com.example.profileservice.experience.repository.ExperienceRepository;
import com.example.profileservice.resume.model.dto.request.ResumeCreateRequest;
import com.example.profileservice.resume.model.dto.request.ResumeUpdateRequest;
import com.example.profileservice.resume.model.dto.response.ResumeSimpleResponse;
import com.example.profileservice.resume.model.entity.ResumeEntity;
import com.example.profileservice.resume.repository.ResumeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestKafkaConfig.class)
@Transactional
public class ResumeControllerTest {

    private static final String BASE_URL = "/api/resumes";
    private static final String TEST_MEMBER_CODE = "member-test-uuid-001";
    private static final String OTHER_MEMBER_CODE = "member-test-uuid-999"; // 권한 테스트용
    private static final String HEADER_X_CODE = "X-CODE";
    private static final String INVALID_MEMBER_CODE = "invalid-member-code-999";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @MockitoBean
    private MemberFeignClient memberFeignClient;

    private ResumeEntity initialResume;
    private ExperienceEntity initialExperience;
    private ResumeCreateRequest createRequest;
    private ExperienceRequest experienceRequest;
    private Instant now;

    @BeforeEach
    void setUp() {

        // Mocking 설정: 기본적으로 모든 유효한 요청에 대해 성공 응답 반환
        List<String> validCodes = List.of(TEST_MEMBER_CODE, OTHER_MEMBER_CODE);
        MemberExistOutput mockExistOutput = new MemberExistOutput(validCodes, List.of());
        ResponseDto<MemberExistOutput> mockSuccessResponse = ResponseDto.success(mockExistOutput);

        // memberServiceClient.existMemberByCode 호출 시 성공 응답 반환하도록 Mocking
        Mockito.when(memberFeignClient.existMemberByCode(anyList()))
                .thenReturn(mockSuccessResponse);

        // 테스트 환경 고정 시간 설정
        now = Instant.parse("2025-11-17T10:00:00Z");

        // 1. 기본 이력서 데이터 설정 (TEST_MEMBER_CODE 소유)
        initialResume = ResumeEntity.builder()
                .memberCode(TEST_MEMBER_CODE)
                .title("기본 이력서 제목")
                .body("기본 이력서 내용")
                .link("http://github.com/test")
                .build();
        resumeRepository.save(initialResume);

        // 2. 기본 경력/경험 데이터 설정 (initialResume에 속함)
        initialExperience = ExperienceEntity.create(
                initialResume.getCode(),
                "초기 프로젝트",
                "초기 팀",
                "초기 설명",
                now.minusSeconds(3600),
                now
        );
        experienceRepository.save(initialExperience);

        // 3. 공통 요청 데이터 설정
        createRequest = new ResumeCreateRequest(
                "새 이력서 제목",
                "새 이력서 내용",
                "http://new.link/notion"
        );
        experienceRequest = new ExperienceRequest(
                "새 경험 활동",
                "새 기관명",
                "새 상세 내용",
                now.minusSeconds(7200),
                now.minusSeconds(3600)
        );
    }

    @AfterEach
    void tearDown() {
        experienceRepository.deleteAll();
        resumeRepository.deleteAll();
    }

    //Resume API 테스트

    @Test
    @DisplayName("POST /api/resumes - 이력서 등록 성공 (201 Created)")
    void createResume_Success() throws Exception {
        // when
        mockMvc.perform(post(BASE_URL)
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value(createRequest.title()))
                .andExpect(jsonPath("$.data.body").value(createRequest.body()))
                .andExpect(jsonPath("$.data.experiences.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/resumes/me - 내 이력서 목록 조회 성공")
    void getMyResumes_Success() throws Exception {
        // given: 다른 회원의 이력서 추가
        ResumeEntity otherResume = ResumeEntity.builder()
                .memberCode(OTHER_MEMBER_CODE)
                .title("다른 사람 이력서")
                .body("내용")
                .link("")
                .build();
        resumeRepository.save(otherResume);

        // when & then
        MvcResult result = mockMvc.perform(get(BASE_URL + "/me")
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andReturn();

        // 결과 검증: 본인 이력서만 조회되었는지 확인
        String responseJson = result.getResponse().getContentAsString();
        ResponseDto<List<ResumeSimpleResponse>> responseDto = objectMapper.readValue(responseJson, new TypeReference<>() {});
        List<String> returnedCodes = responseDto.getData().stream().map(ResumeSimpleResponse::resumeCode).collect(
                Collectors.toList());

        assertThat(returnedCodes).containsExactly(initialResume.getCode());
    }

    @Test
    @DisplayName("GET /api/resumes/{resumeCode} - 이력서 상세 조회 성공 (권한 OK)")
    void getResumeDetail_Success() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/{resumeCode}", initialResume.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.resumeCode").value(initialResume.getCode()))
                .andExpect(jsonPath("$.data.experiences.length()").value(1))
                .andExpect(jsonPath("$.data.experiences[0].experienceCode").value(initialExperience.getCode()));
    }

    @Test
    @DisplayName("GET /api/resumes/{resumeCode} - 다른 회원의 이력서 조회 시 403 Forbidden")
    void getResumeDetail_Unauthorized_Failure() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/{resumeCode}", initialResume.getCode())
                        .header(HEADER_X_CODE, OTHER_MEMBER_CODE)) // 다른 회원의 X-CODE 사용
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(3302)); // UNAUTHORIZED_RESUME_ACCESS
    }

    @Test
    @DisplayName("PATCH /api/resumes/{resumeCode} - 이력서 수정 성공")
    void updateResume_Success() throws Exception {
        // given
        ResumeUpdateRequest updateRequest = new ResumeUpdateRequest("수정된 제목", "수정된 내용", "http://updated.link");

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{resumeCode}", initialResume.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.body").value("수정된 내용"))
                .andExpect(jsonPath("$.data.link").value("http://updated.link"));
    }

    @Test
    @DisplayName("DELETE /api/resumes/{resumeCode} - 이력서 삭제 성공 (Soft Delete)")
    void deleteResume_Success() throws Exception {
        // when
        mockMvc.perform(delete(BASE_URL + "/{resumeCode}", initialResume.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // DB에서 isDeleted가 true로 변경되었는지 확인
        assertThat(resumeRepository.findByCodeAndIsDeletedFalse(initialResume.getCode())).isEmpty();
        // 삭제 후에도 실제 엔티티는 남아있는지 확인
        assertThat(resumeRepository.findById(initialResume.getId())).isPresent();
        assertThat(resumeRepository.findById(initialResume.getId()).get().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("POST /api/resumes - 유효하지 않은 회원 코드로 등록 시도 시 400 Bad Request")
    void createResume_InvalidMemberCode_Failure() throws Exception {
        // given: 유효하지 않은 회원 코드를 시뮬레이션하기 위한 Mocking 설정

        // 1. Mock 응답 데이터 (존재하지 않는 코드 목록)
        MemberExistOutput mockExistOutputFailure = new MemberExistOutput(
                List.of(),
                List.of(INVALID_MEMBER_CODE) // 요청 코드가 존재하지 않음
        );
        ResponseDto<MemberExistOutput> mockFailureResponse = ResponseDto.success(mockExistOutputFailure);

        // 2. Mocking 실행: INVALID_MEMBER_CODE가 포함된 모든 호출에 대해 실패 응답을 반환하도록 설정
        Mockito.when(memberFeignClient.existMemberByCode(Mockito.argThat(
                        codes -> codes.contains(INVALID_MEMBER_CODE) // <-- 이 코드가 포함된 List 호출을 잡음
                )))
                .thenReturn(mockFailureResponse);

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .header(HEADER_X_CODE, INVALID_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(3006)); // INVALID_MEMBER_CODE (프로필 서비스 명세)

        // 3. (선택) verify
        Mockito.verify(memberFeignClient, Mockito.times(1))
                .existMemberByCode(Mockito.argThat(codes -> codes.contains(INVALID_MEMBER_CODE)));
    }

    //Experience API 테스트

    @Test
    @DisplayName("POST /api/resumes/{resumeCode}/experiences - 경력/경험 등록 성공")
    void createExperience_Success() throws Exception {
        // when
        mockMvc.perform(post(BASE_URL + "/{resumeCode}/experiences", initialResume.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experienceRequest)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(experienceRequest.title()))
                .andExpect(jsonPath("$.data.organization").value(experienceRequest.organization()));

        // DB에 실제로 저장되었는지 확인
        assertThat(experienceRepository.findAllByResumeCodeAndIsDeletedFalseOrderByStartedAtDesc(initialResume.getCode())).hasSize(2);
    }

    @Test
    @DisplayName("POST /api/resumes/{resumeCode}/experiences - 타인 이력서에 경력 등록 시 403 Forbidden")
    void createExperience_UnauthorizedResume_Failure() throws Exception {
        // given: 타인의 이력서 생성 (권한 없음)
        ResumeEntity otherMemberResume = ResumeEntity.builder()
                .memberCode(OTHER_MEMBER_CODE)
                .title("타인 이력서")
                .body("내용")
                .link("")
                .build();
        resumeRepository.save(otherMemberResume);

        // when & then: 본인 코드로 타인의 이력서에 경험 등록 시도
        mockMvc.perform(post(BASE_URL + "/{resumeCode}/experiences", otherMemberResume.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(experienceRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(3302)); // UNAUTHORIZED_RESUME_ACCESS
    }

    @Test
    @DisplayName("PATCH /api/resumes/{resumeCode}/experiences/{experienceCode} - 경력/경험 수정 성공")
    void updateExperience_Success() throws Exception {
        // given
        ExperienceRequest updateRequest = new ExperienceRequest(
                "수정된 활동명",
                "수정된 기관",
                "수정된 상세 내용",
                initialExperience.getStartedAt(),
                initialExperience.getEndedAt()
        );

        // when
        mockMvc.perform(patch(BASE_URL + "/{resumeCode}/experiences/{experienceCode}", initialResume.getCode(), initialExperience.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정된 활동명"))
                .andExpect(jsonPath("$.data.organization").value("수정된 기관"));

        // DB에서 실제로 수정되었는지 확인
        ExperienceEntity updatedEntity = experienceRepository.findByCodeAndResumeCodeAndIsDeletedFalse(initialExperience.getCode(), initialResume.getCode()).orElseThrow();
        assertThat(updatedEntity.getTitle()).isEqualTo("수정된 활동명");
    }

    @Test
    @DisplayName("PATCH /api/resumes/{resumeCode}/experiences/{experienceCode} - 존재하지 않는 경력 수정 시 404 Not Found")
    void updateExperience_ExperienceNotFound_Failure() throws Exception {
        // given
        String nonExistentCode = "non-existent-code";
        ExperienceRequest updateRequest = new ExperienceRequest("수정", "수정", "수정", now, now);

        // when & then
        mockMvc.perform(patch(BASE_URL + "/{resumeCode}/experiences/{experienceCode}", initialResume.getCode(), nonExistentCode)
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3101)); // EXPERIENCE_NOT_FOUND
    }

    @Test
    @DisplayName("DELETE /api/resumes/{resumeCode}/experiences/{experienceCode} - 경력/경험 삭제 성공 (Soft Delete)")
    void deleteExperience_Success() throws Exception {
        // when
        mockMvc.perform(delete(BASE_URL + "/{resumeCode}/experiences/{experienceCode}", initialResume.getCode(), initialExperience.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // DB에서 Soft Delete 확인
        assertThat(experienceRepository.findByCodeAndResumeCodeAndIsDeletedFalse(initialExperience.getCode(), initialResume.getCode())).isEmpty();
        assertThat(experienceRepository.findById(initialExperience.getId()).get().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("DELETE /api/resumes/{resumeCode}/experiences/{experienceCode} - 타인 이력서의 경력 삭제 시도 시 403 Forbidden")
    void deleteExperience_UnauthorizedResume_Failure() throws Exception {
        // when & then
        mockMvc.perform(delete(BASE_URL + "/{resumeCode}/experiences/{experienceCode}", initialResume.getCode(), initialExperience.getCode())
                        .header(HEADER_X_CODE, OTHER_MEMBER_CODE)) // 타인 코드로 시도
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(3302)); // UNAUTHORIZED_RESUME_ACCESS
    }

    @Test
    @DisplayName("DELETE /api/resumes/{resumeCode}/experiences/{experienceCode} - 존재하지 않는 경력 삭제 시 404 Not Found")
    void deleteExperience_NotFound_Failure() throws Exception {
        // when & then
        mockMvc.perform(delete(BASE_URL + "/{resumeCode}/experiences/{experienceCode}", initialResume.getCode(), "non-existent-code")
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3101)); // EXPERIENCE_NOT_FOUND
    }
}
