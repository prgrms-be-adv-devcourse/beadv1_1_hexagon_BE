package com.example.memberservice.member.controller.dto.response;


import com.example.memberservice.common.client.dto.response.s3.PresignedDownloadResponse;
import com.example.memberservice.member.service.model.vo.ApiMemberInfo;
import com.example.memberservice.member.service.model.vo.MemberRating;
import com.example.memberservice.member.service.model.vo.MemberTag;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record MemberGetResponse(
    @Schema(description = "사용자 정보")
    ApiMemberInfo info,
    
    @Schema(description = "사용자 평가 정보")
    MemberRating rating,

    @Schema(description = "사용자 기술 태그 정보")
    List<MemberTag> tags,

    @Schema(description = "사용자 프로필 이미지")
    List<PresignedDownloadResponse> images
) {

}
