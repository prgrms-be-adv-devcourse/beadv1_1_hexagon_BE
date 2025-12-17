package com.example.profileservice.resume.api;

import com.example.profileservice.experience.model.dto.request.ExperienceRequest;
import com.example.profileservice.experience.model.dto.response.ExperienceResponse;
import com.example.profileservice.resume.model.dto.request.ResumeCreateRequest;
import com.example.profileservice.resume.model.dto.request.ResumeUpdateRequest;
import com.example.profileservice.resume.model.dto.response.ResumeDetailResponse;
import com.example.profileservice.resume.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController implements ResumeApiController {

    private final ResumeService resumeService;

    // 본인용 조회
    @Override
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> getMyResume(
            @RequestHeader(value = "X-CODE") String memberCode
    ) {
        ResumeDetailResponse resume = resumeService.getMyResume(memberCode);
        return ResponseEntity.ok(ResponseDto.success(resume));
    }

    // 이력서 등록
    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> createResume(
            @RequestHeader(value = "X-CODE") String memberCode,
            @Valid @RequestBody ResumeCreateRequest request) {
        ResumeDetailResponse response = resumeService.createResume(memberCode, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(response));
    }

    // 이력서 상세 조회
    @Override
    @GetMapping("/{resumeCode}")
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> getResumeDetail(
            @PathVariable String resumeCode) {
        ResumeDetailResponse response = resumeService.getPublicResumeDetail(resumeCode);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    // 이력서 수정
    @Override
    @PatchMapping("/{resumeCode}")
    public ResponseEntity<ResponseDto<ResumeDetailResponse>> updateResume(
            @RequestHeader(value = "X-CODE") String memberCode,
            @PathVariable String resumeCode,
            @Valid @RequestBody ResumeUpdateRequest request) {
        ResumeDetailResponse response = resumeService.updateResume(memberCode, resumeCode, request);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    // 이력서 삭제
    @Override
    @DeleteMapping("/{resumeCode}")
    public ResponseEntity<ResponseDto<Empty>> deleteResume(
            @RequestHeader(value = "X-CODE") String memberCode,
            @PathVariable String resumeCode
    ) {
        resumeService.deleteResume(memberCode, resumeCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }

    // 경력/경험 등록
    @Override
    @PostMapping("/{resumeCode}/experiences")
    public ResponseEntity<ResponseDto<ExperienceResponse>> createExperience(
            @RequestHeader(value = "X-CODE") String memberCode,
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
            @RequestHeader(value = "X-CODE") String memberCode,
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
            @RequestHeader(value = "X-CODE") String memberCode,
            @PathVariable String resumeCode,
            @PathVariable String experienceCode
    ) {
        resumeService.deleteExperience(memberCode, resumeCode, experienceCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }
}
