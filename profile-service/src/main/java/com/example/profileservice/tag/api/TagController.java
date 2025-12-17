package com.example.profileservice.tag.api;

import com.example.profileservice.tag.model.dto.request.TagRequest;
import com.example.profileservice.tag.model.dto.response.TagResponse;
import com.example.profileservice.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController implements TagApiController {

    // Gateway 환경이 구축되지 않았을 때를 위한 임시 기본값
    private static final String DEFAULT_MEMBER_CODE = "member-uuid-code-001";

    private final TagService tagService;

    // 전체 태그 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<ResponseDto<List<TagResponse>>> getAllTags() {
        List<TagResponse> tags = tagService.getAllTags();

        return ResponseEntity.ok(ResponseDto.success(tags));
    }

    // 새 태그 등록
    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<TagResponse>> createTag(@Valid @RequestBody TagRequest request) {
        TagResponse newTag = tagService.createTag(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(newTag));
    }

    // 회원 태그 목록 조회
    @Override
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<List<TagResponse>>> getMyTags(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode
    ) {
        List<TagResponse> myTags = tagService.getMyTags(memberCode);

        return ResponseEntity.ok(ResponseDto.success(myTags));
    }

    // 회원 태그 연결
    @Override
    @PostMapping("/{tagCode}/members/me")
    public ResponseEntity<ResponseDto<Empty>> linkMemberTag(@PathVariable String tagCode,
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode) {
        tagService.linkMemberTag(memberCode, tagCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }

    // 회원 태그 연결 해제
    @Override
    @DeleteMapping("/{tagCode}/members/me")
    public ResponseEntity<ResponseDto<Empty>> unlinkMemberTag(@PathVariable String tagCode,
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode) {
        tagService.unlinkMemberTag(memberCode, tagCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }

    // 회원 태그 동기화
    @Override
    @PutMapping("/members/me")
    public ResponseEntity<ResponseDto<Empty>> syncMemberTags(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @RequestBody List<String> tagCodes) {
        tagService.syncMemberTags(memberCode, tagCodes);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }

    // 기술명으로 태그 정보 조회
    @Override
    @GetMapping("/by-skill")
    public ResponseEntity<ResponseDto<TagResponse>> getTagBySkill(@RequestParam("skill") String skill) {
        TagResponse tag = tagService.getTagBySkill(skill);

        return ResponseEntity.ok(ResponseDto.success(tag));
    }
}
