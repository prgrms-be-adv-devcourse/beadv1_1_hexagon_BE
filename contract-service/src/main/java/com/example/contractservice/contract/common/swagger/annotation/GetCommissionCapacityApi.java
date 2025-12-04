package com.example.contractservice.contract.common.swagger.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "의뢰글 수용 인원 조회", description = "특정 의뢰글에 해당하는 최대 지원 인원, 현재 지원 인원, 최대 선정 인원, 현재 선정 인원을 조회")
@Parameters({
        @Parameter(name = "commission-code", description = "의뢰글 코드", in = ParameterIn.PATH, required = true)
})
public @interface GetCommissionCapacityApi {

}
