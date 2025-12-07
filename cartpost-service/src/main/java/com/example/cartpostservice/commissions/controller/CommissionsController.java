package com.example.cartpostservice.commissions.controller;

import static com.example.cartpostservice.common.model.dto.ResponseDtoMapper.getSuccessResponse;

import com.example.cartpostservice.commissions.controller.dto.request.CommissionCreateRequest;
import com.example.cartpostservice.commissions.controller.dto.request.CommissionUpdateRequest;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionCreateResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionElementReadResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionUpdateResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionReadResponse;
import com.example.cartpostservice.commissions.service.CommissionsManagerService;
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

    private final CommissionsManagerService commissionsManagerService;

    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<CommissionCreateResponse>> createCommission(@RequestHeader("X-CODE") String code,
            @Valid @RequestBody CommissionCreateRequest commissionCreateRequest) {

        CommissionCreateResponse response = commissionsManagerService.createCommission(code, commissionCreateRequest);
        CustomStatusCode createdStatus = CustomStatusCode.CREATED;

        return new ResponseEntity<>(getSuccessResponse(createdStatus, response),
                createdStatus.getStatus());
    }

    @Override
    @GetMapping("/{commission-code}")
    public ResponseEntity<ResponseDto<CommissionElementReadResponse>> readCommission(
            @PathVariable(name = "commission-code") String commissionCode) {

        CommissionElementReadResponse response = commissionsManagerService.readCommission(commissionCode);

        return new ResponseEntity<>(getSuccessResponse(CustomStatusCode.SUCCESS, response),
                CustomStatusCode.SUCCESS.getStatus());
    }

    @Override
    @PatchMapping("/{commission-code}")
    public ResponseEntity<ResponseDto<CommissionUpdateResponse>> updateCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode,
            @RequestBody CommissionUpdateRequest commissionUpdateRequest
    ) {

        CommissionUpdateResponse response = commissionsManagerService.updateCommission(code, commissionCode,
                commissionUpdateRequest);

        return new ResponseEntity<>(getSuccessResponse(CustomStatusCode.SUCCESS, response),
                CustomStatusCode.SUCCESS.getStatus());
    }

    @Override
    @DeleteMapping("/{commission-code}")
    public ResponseEntity<ResponseDto<Empty>> deleteCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode) {

        commissionsManagerService.deleteCommission(code, commissionCode);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }

    @Override
    @PatchMapping("/deadline/{commission-code}")
    public ResponseEntity<ResponseDto<Empty>> finishCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode) {

        commissionsManagerService.finishCommission(code, commissionCode);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }

    @Override
    @GetMapping("/total")
    public ResponseEntity<ResponseDto<Page<CommissionReadResponse>>> readOwnCommissions(
            @RequestHeader("X-CODE") String code,
            Pageable pageable) {

        Page<CommissionReadResponse> responses = commissionsManagerService.readOwnCommissions(code, pageable);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, responses));
    }

    @Override
    @GetMapping("/exist/{commission-code}")
    public ResponseEntity<ResponseDto<Empty>> canAccessCommission(@RequestHeader("X-CODE") String code,
            @PathVariable(name = "commission-code") String commissionCode) {

        commissionsManagerService.canAccessCommission(code, commissionCode);

        return ResponseEntity.status(CustomStatusCode.SUCCESS.getStatus())
                .body(getSuccessResponse(CustomStatusCode.SUCCESS, Empty.getInstance()));
    }

}
