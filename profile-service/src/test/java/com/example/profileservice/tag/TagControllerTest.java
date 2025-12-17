package com.example.profileservice.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.profileservice.common.model.vo.KafkaProducer;
import com.example.profileservice.tag.model.dto.request.TagRequest;
import com.example.profileservice.tag.model.dto.response.TagResponse;
import com.example.profileservice.tag.model.entity.MemberTagEntity;
import com.example.profileservice.tag.model.entity.TagEntity;
import com.example.profileservice.tag.repository.MemberTagRepository;
import com.example.profileservice.tag.repository.TagRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.hexagon.core.dto.ResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TagControllerTest {

    private static final String BASE_URL = "/api/tags";
    private static final String TEST_MEMBER_CODE = "member-test-uuid-001";
    private static final String HEADER_X_CODE = "X-CODE";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private MemberTagRepository memberTagRepository;

    @MockitoBean
    private KafkaProducer kafkaProducer;

    private TagEntity springTag;
    private TagEntity javaTag;

    @BeforeEach
    void setUp() {
        // KafkaProducer Mocking 설정
        // send 메서드가 호출될 때 아무 작업도 하지 않도록 설정
        // TagService와 SelfPromotionService가 KafkaProducer를 사용하기 때문에 필요
        doNothing().when(kafkaProducer).send(any(String.class), any());

        // 테스트 전용 기본 데이터 설정
        springTag = TagEntity.builder().skill("Spring Boot").build();
        javaTag = TagEntity.builder().skill("Java").build();
        tagRepository.saveAll(Arrays.asList(springTag, javaTag));
    }

    @AfterEach
    void tearDown() {
        // create-drop 덕분에 사실상 필요 없지만 명시적으로 정리
        memberTagRepository.deleteAll();
        tagRepository.deleteAll();
    }

    // 태그 생성 및 조회 테스트
    @Test
    @DisplayName("POST /api/tags - 새 기술 태그 등록 성공")
    void createTag_Success() throws Exception {
        // given
        TagRequest request = new TagRequest("Kotlin");

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.skill").value("Kotlin"));

        assertThat(tagRepository.existsBySkillIgnoreCase("Kotlin")).isTrue();
    }

    @Test
    @DisplayName("POST /api/tags - 중복 태그 등록 시 409 Conflict 반환")
    void createTag_Duplicate_Failure() throws Exception {
        // given
        TagRequest request = new TagRequest("Spring Boot"); // 이미 존재하는 태그

        // when & then
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(3502)) // TAG_ALREADY_EXISTS
                .andExpect(jsonPath("$.message").value("이미 존재하는 기술 태그입니다."));
    }

    @Test
    @DisplayName("GET /api/tags - 전체 태그 목록 조회 성공")
    void getAllTags_Success() throws Exception {
        // when
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].skill").value("Spring Boot"))
                .andExpect(jsonPath("$.data[1].skill").value("Java"));
    }

    @Test
    @DisplayName("GET /api/tags/by-skill - 기술 이름으로 태그 조회 성공")
    void getTagBySkill_Success() throws Exception {
        // given
        String skillName = "Java";

        // when & then
        mockMvc.perform(get(BASE_URL + "/by-skill")
                        .param("skill", skillName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skill").value(skillName))
                .andExpect(jsonPath("$.data.tagCode").value(javaTag.getCode()));
    }

    @Test
    @DisplayName("GET /api/tags/by-skill - 존재하지 않는 기술 이름으로 조회 시 404 Not Found")
    void getTagBySkill_NotFound_Failure() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/by-skill")
                        .param("skill", "NonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3501)); // TAG_NOT_FOUND
    }

    // 회원 태그 연결/해제 테스트
    @Test
    @DisplayName("POST /api/tags/{tagCode}/members/me - 회원 태그 연결 성공")
    void linkMemberTag_Success() throws Exception {
        // given
        String tagCodeToLink = springTag.getCode();

        // when & then
        mockMvc.perform(post(BASE_URL + "/{tagCode}/members/me", tagCodeToLink)
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        assertThat(memberTagRepository.existsByMemberCodeAndTagCode(TEST_MEMBER_CODE, tagCodeToLink)).isTrue();
    }

    @Test
    @DisplayName("POST /api/tags/{tagCode}/members/me - 존재하지 않는 태그 연결 시 404 Not Found")
    void linkMemberTag_TagNotFound_Failure() throws Exception {
        // given
        String nonExistentCode = "non-existent-tag-code";

        // when & then
        mockMvc.perform(post(BASE_URL + "/{tagCode}/members/me", nonExistentCode)
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3501)); // TAG_NOT_FOUND
    }

    @Test
    @DisplayName("DELETE /api/tags/{tagCode}/members/me - 회원 태그 연결 해제 성공")
    void unlinkMemberTag_Success() throws Exception {
        // given: 먼저 태그를 연결
        memberTagRepository.save(MemberTagEntity.create(TEST_MEMBER_CODE, springTag.getCode()));
        assertThat(memberTagRepository.existsByMemberCodeAndTagCode(TEST_MEMBER_CODE, springTag.getCode())).isTrue();

        // when & then
        mockMvc.perform(delete(BASE_URL + "/{tagCode}/members/me", springTag.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        assertThat(memberTagRepository.existsByMemberCodeAndTagCode(TEST_MEMBER_CODE, springTag.getCode())).isFalse();
    }

    @Test
    @DisplayName("DELETE /api/tags/{tagCode}/members/me - 연결되지 않은 태그 해제 시 404 Not Found")
    void unlinkMemberTag_NotFound_Failure() throws Exception {
        // when & then
        mockMvc.perform(delete(BASE_URL + "/{tagCode}/members/me", javaTag.getCode())
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3504)); // MEMBER_TAG_NOT_FOUND
    }

    // 회원 태그 목록 조회 및 동기화 테스트
    @Test
    @DisplayName("GET /api/tags/me - 회원 태그 목록 조회 성공")
    void getMyTags_Success() throws Exception {
        // given: 여러 태그를 연결
        memberTagRepository.save(MemberTagEntity.create(TEST_MEMBER_CODE, springTag.getCode()));
        memberTagRepository.save(MemberTagEntity.create(TEST_MEMBER_CODE, javaTag.getCode()));

        // when
        MvcResult result = mockMvc.perform(get(BASE_URL + "/me")
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andReturn();

        // then: 응답 받은 코드가 실제로 연결된 태그 코드와 일치하는지 확인
        String responseJson = result.getResponse().getContentAsString();
        ResponseDto<List<TagResponse>> responseDto = objectMapper.readValue(responseJson, new com.fasterxml.jackson.core.type.TypeReference<ResponseDto<List<TagResponse>>>() {});

        List<String> returnedCodes = responseDto.data().stream().map(TagResponse::tagCode).collect(Collectors.toList());
        assertThat(returnedCodes).containsExactlyInAnyOrder(springTag.getCode(), javaTag.getCode());
    }

    @Test
    @DisplayName("PUT /api/tags/members/me - 회원 태그 목록 동기화 성공 (추가 및 삭제)")
    void syncMemberTags_AddAndDelete_Success() throws Exception {
        // given
        // 1. 초기 상태: Spring Boot만 연결
        memberTagRepository.save(MemberTagEntity.create(TEST_MEMBER_CODE, springTag.getCode()));

        // 2. 새로운 태그 생성 (추가 대상)
        TagEntity pythonTag = TagEntity.builder().skill("Python").build();
        tagRepository.save(pythonTag);

        // 3. 요청 목록: Java(새 연결), Python(추가 연결), Spring Boot(유지) -> Java(삭제 대상)
        List<String> requestedCodes = Arrays.asList(javaTag.getCode(), pythonTag.getCode());

        // when
        mockMvc.perform(put(BASE_URL + "/members/me")
                        .header(HEADER_X_CODE, TEST_MEMBER_CODE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestedCodes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        // then
        // 1. 삭제되었는지 확인: Spring Boot는 요청 목록에 없으므로 삭제되어야 함
        assertThat(memberTagRepository.existsByMemberCodeAndTagCode(TEST_MEMBER_CODE, springTag.getCode())).isFalse();
        // 2. 추가되었는지 확인: Java와 Python은 추가되어야 함
        assertThat(memberTagRepository.existsByMemberCodeAndTagCode(TEST_MEMBER_CODE, javaTag.getCode())).isTrue();
        assertThat(memberTagRepository.existsByMemberCodeAndTagCode(TEST_MEMBER_CODE, pythonTag.getCode())).isTrue();
        // 3. 총 연결 개수 확인
        assertThat(memberTagRepository.findAllByMemberCode(TEST_MEMBER_CODE)).hasSize(2);
    }

    @Test
    @DisplayName("GET /api/tags/by-codes - 태그 코드 목록으로 일괄 조회 성공")
    void getTagsByCodes_Success() throws Exception {
        // given
        // 콤마(,)로 구분된 파라미터 생성
        String codesParam = springTag.getCode() + "," + javaTag.getCode();

        // when & then
        mockMvc.perform(get(BASE_URL + "/by-codes")
                        .param("codes", codesParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[?(@.tagCode == '%s')].skill", springTag.getCode()).value("Spring Boot"))
                .andExpect(jsonPath("$.data[?(@.tagCode == '%s')].skill", javaTag.getCode()).value("Java"));
    }

    @Test
    @DisplayName("GET /api/tags/by-codes - 존재하지 않는 코드가 섞여 있어도 존재하는 것만 반환")
    void getTagsByCodes_PartialSuccess() throws Exception {
        // given
        String codesParam = springTag.getCode() + ",non-existent-code";

        // when & then
        mockMvc.perform(get(BASE_URL + "/by-codes")
                        .param("codes", codesParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].skill").value("Spring Boot"));
    }

    @Test
    @DisplayName("GET /api/tags/by-codes - 빈 리스트 전달 시 빈 데이터 반환")
    void getTagsByCodes_EmptyList_Success() throws Exception {
        // when & then
        mockMvc.perform(get(BASE_URL + "/by-codes")
                        .param("codes", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
