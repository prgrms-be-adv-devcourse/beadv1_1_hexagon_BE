package com.example.memberservice.common.client;


import com.example.memberservice.common.client.dto.response.tag.TagResponse;
import java.util.List;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    name = "profile-service",
    contextId = "profileTagService",
    path = "/api/tags"
)
public interface TagServiceClient {
    @GetMapping("/me")
    ResponseDto<List<TagResponse>> getMyTags(
        @RequestHeader("X-CODE") String memberCode
    );
}
