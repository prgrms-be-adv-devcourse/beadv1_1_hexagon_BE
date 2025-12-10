package com.example.cartpostservice.commissions.controller.internal;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getSuccessResponse;

import com.example.cartpostservice.commissions.controller.internal.dto.request.RecruitmentStatusRequest;
import com.example.cartpostservice.commissions.controller.internal.dto.response.CommissionRecruitmentStatusResponse;
import com.example.cartpostservice.commissions.service.CommissionsManagerService;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/commissions")
@RequiredArgsConstructor
public class CommissionInternalController implements CommissionInternalApi {

    private final CommissionsManagerService commissionsManagerService;

    @Override
    @GetMapping("/recruitment-status")
    public ResponseEntity<ResponseDto<CommissionRecruitmentStatusResponse>> getRecruitmentStatus(
            @Valid @RequestBody RecruitmentStatusRequest recruitmentStatusRequest) {

        CommissionRecruitmentStatusResponse statusResponse = commissionsManagerService.getRecruitmentStatus(
                recruitmentStatusRequest);

        return new ResponseEntity<>(getSuccessResponse(CustomStatusCode.SUCCESS, statusResponse), HttpStatus.OK);
    }
}
