package com.example.cartpostservice.commissions.controller.internal;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getSuccessResponse;

import com.example.cartpostservice.commissions.controller.internal.dto.response.CommissionRecruitmentStatusResponse;
import com.example.cartpostservice.commissions.service.DomainCompositeService;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/commissions")
@Validated
@RequiredArgsConstructor
public class CommissionInternalController implements CommissionInternalApi {

    private final DomainCompositeService domainCompositeService;

    @Override
    @GetMapping("/recruitment-status")
    public ResponseEntity<ResponseDto<CommissionRecruitmentStatusResponse>> getRecruitmentStatus(
            @NotBlank(message = "의뢰글 uuid를 전달해 주시기 바랍니다")
            @PathVariable("commission-code") String commissionsCode) {

        CommissionRecruitmentStatusResponse statusResponse = domainCompositeService.getRecruitmentStatus(
                commissionsCode);

        return new ResponseEntity<>(getSuccessResponse(CustomStatusCode.SUCCESS, statusResponse), HttpStatus.OK);
    }
}
