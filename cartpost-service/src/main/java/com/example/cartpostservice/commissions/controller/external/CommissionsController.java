package com.example.cartpostservice.commissions.controller.external;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getSuccessResponse;

import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionCreateRequest;
import com.example.cartpostservice.commissions.controller.external.dto.request.CommissionUpdateRequest;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionCreateResponse;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionElementReadResponse;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionReadResponse;
import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionUpdateResponse;
import com.example.cartpostservice.commissions.service.OrchestrationService;
import com.example.cartpostservice.commissions.service.mapper.CommissionResponseAssembler;
import com.example.cartpostservice.commissions.service.usecase.result.CommissionElementResult;
import com.example.cartpostservice.common.exception.CustomStatusCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/commissions")
@RequiredArgsConstructor
public class CommissionsController implements CommissionsApi {

    private final OrchestrationService orchestrationService;
    private final CommissionResponseAssembler commissionResponseAssembler;

    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<CommissionCreateResponse>> createCommission(@RequestHeader("X-CODE") String code,
            @Valid @RequestBody CommissionCreateRequest commissionCreateRequest) {

        CommissionCreateResponse response = orchestrationService.createCommission(code, commissionCreateRequest);
        CustomStatusCode createdStatus = CustomStatusCode.CREATED;

        return new ResponseEntity<>(getSuccessResponse(createdStatus, response),
                createdStatus.getStatus());
    }

    @Override
    @GetMapping("/{commission-code}")
    public ResponseEntity<ResponseDto<CommissionElementReadResponse>> readCommission(
            @PathVariable(name = "commission-code") String commissionCode) {

        CommissionElementResult result = orchestrationService.readCommission(commissionCode);
        CommissionElementReadResponse response = commissionResponseAssembler.toReadResponse(result);

        return new ResponseEntity<>(getSuccessResponse(CustomStatusCode.SUCCESS, response),
                CustomStatusCode.SUCCESS.getStatus());
    }

    public ResponseEntity<ResponseDto<Empty>> readFiles(
            @PathVariable(name = "commission-code") String commissionCode) {
        // 의뢰글 관련 파일 읽기만 따로 분리
        return null;
    }

    @Override
    @PatchMapping("/{commission-code}")
    public ResponseEntity<ResponseDto<CommissionUpdateResponse>> updateCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode,
            @Valid @RequestBody CommissionUpdateRequest commissionUpdateRequest
    ) {

        CommissionUpdateResponse response = orchestrationService.updateCommission(code, commissionCode,
                commissionUpdateRequest);

        return new ResponseEntity<>(getSuccessResponse(CustomStatusCode.SUCCESS, response),
                CustomStatusCode.SUCCESS.getStatus());
    }

    @Override
    @DeleteMapping("/{commission-code}")
    public ResponseEntity<ResponseDto<Empty>> deleteCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode) {

        orchestrationService.deleteCommission(code, commissionCode);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }

    @Override
    @PatchMapping("/deadline/{commission-code}")
    public ResponseEntity<ResponseDto<Empty>> finishCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode) {

        orchestrationService.finishCommission(code, commissionCode);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }

    @Override
    public ResponseEntity<ResponseDto<Empty>> openCommission(@RequestHeader("X-CODE") String memberCode,
            String commissionCode) {

        orchestrationService.openCommission(memberCode, commissionCode);
        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }


    @Override
    @GetMapping("/total")
    public ResponseEntity<ResponseDto<Page<CommissionReadResponse>>> readOwnCommissions(
            @RequestHeader("X-CODE") String code,
            Pageable pageable) {

        Page<CommissionReadResponse> responses = orchestrationService.readOwnCommissions(code, pageable);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, responses));
    }

    @Override
    @GetMapping("/exist/{commission-code}")
    public ResponseEntity<ResponseDto<Empty>> canAccessCommission(@RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode) {

        orchestrationService.canAccessCommission(code, commissionCode);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }

}
