package com.example.recommendationservice.client;

import com.example.recommendationservice.client.dto.output.ProfileReadOutput;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name: 호출 대상 서비스 이름, path: 기본 경로
@FeignClient(
    name = "profile-service",
    path = "/internal/profiles"
)
public interface ProfileServiceClient {

    // 프리랜서 프로필 조회
    @GetMapping("/freelancers/{freelancer-code}")
    ResponseDto<ProfileReadOutput> getFreelancerProfile(
        @PathVariable("freelancer-code") String freelancerCode
    );

}
