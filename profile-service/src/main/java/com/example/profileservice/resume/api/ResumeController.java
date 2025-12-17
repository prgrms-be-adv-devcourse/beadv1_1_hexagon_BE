package com.example.profileservice.resume.api;

import com.example.profileservice.experience.model.dto.request.ExperienceRequest;
import com.example.profileservice.experience.model.dto.response.ExperienceResponse;
import com.example.profileservice.resume.model.dto.request.ResumeCreateRequest;
import com.example.profileservice.resume.model.dto.request.ResumeUpdateRequest;
import com.example.profileservice.resume.model.dto.response.ResumeDetailResponse;
import com.example.profileservice.resume.model.dto.response.ResumeSimpleResponse;
import com.example.profileservice.resume.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.dto.Empty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController implements ResumeApiController {

    // Gateway 환경이 구축되지 않았을 때를 위한 임시 기본값
    private static final String DEFAULT_MEMBER_CODE = "member-uuid-code-001";

    private final ResumeService resumeService;

    // 이력서 목록 조회
    @Override
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<List<ResumeSimpleResponse>>> getMyResumes(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode
    ) {
        List<ResumeSimpleResponse> resumes = resumeService.getMyResumes(memberCode);

        return ResponseEntity.ok(ResponseDto.success(resumes));
    }

    // 이력서 등록
    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> createResume(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @Valid @RequestBody ResumeCreateRequest request) {
        ResumeDetailResponse response = resumeService.createResume(memberCode, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(response));
    }

    // 이력서 상세 조회
    @Override
    @GetMapping("/{resumeCode}")
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> getResumeDetail(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String resumeCode) {
        ResumeDetailResponse response = resumeService.getResumeDetail(memberCode, resumeCode);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    // 이력서 수정
    @Override
    @PatchMapping("/{resumeCode}")
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> updateResume(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String resumeCode,
            @Valid @RequestBody ResumeUpdateRequest request) {
        ResumeDetailResponse response = resumeService.updateResume(memberCode, resumeCode, request);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    // 이력서 삭제
    @Override
    @DeleteMapping("/{resumeCode}")
    public ResponseEntity<ResponseDto<Empty>> deleteResume(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String resumeCode
    ) {
        resumeService.deleteResume(memberCode, resumeCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }

    // 경력/경험 등록
    @Override
    @PostMapping("/{resumeCode}/experiences")
    public ResponseEntity<ResponseDto<ExperienceResponse>> createExperience(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String resumeCode,
            @Valid @RequestBody ExperienceRequest request
    ) {
        ExperienceResponse response = resumeService.createExperience(memberCode, resumeCode, request);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    // 경력/경험 수정
    @Override
    @PatchMapping("/{resumeCode}/experiences/{experienceCode}")
    public ResponseEntity<ResponseDto<ExperienceResponse>> updateExperience(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String resumeCode,
            @PathVariable String experienceCode,
            @Valid @RequestBody ExperienceRequest request
    ) {
        ExperienceResponse response = resumeService.updateExperience(memberCode, resumeCode, experienceCode, request);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    // 경력/경험 삭제
    @Override
    @DeleteMapping("/{resumeCode}/experiences/{experienceCode}")
    public ResponseEntity<ResponseDto<Empty>> deleteExperience(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String resumeCode,
            @PathVariable String experienceCode
    ) {
        resumeService.deleteExperience(memberCode, resumeCode, experienceCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }
}
