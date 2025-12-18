package com.example.profileservice.profile.api;

import com.example.profileservice.profile.model.dto.response.ProfileReadResponse;
import com.example.profileservice.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/freelancers/{freelancer-code}")
    public ResponseDto<ProfileReadResponse> getFreelancerProfile(
        @PathVariable("freelancer-code") String freelancerCode
    ) {
        ProfileReadResponse response = profileService.findProfileByCode(freelancerCode);
        return ResponseDto.success(response);
    }

}
