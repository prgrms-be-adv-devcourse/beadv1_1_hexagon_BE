package com.example.memberservice.common.client;

import com.example.memberservice.common.client.dto.response.rating.RatingResponse;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "profile-service",
    contextId = "profileRatingService",
    path = "/api/ratings"
)
public interface RatingServiceClient {
    @GetMapping("/{memberCode}")
    ResponseDto<RatingResponse> getMemberRating(
        @PathVariable("memberCode") String memberCode
    );
}
