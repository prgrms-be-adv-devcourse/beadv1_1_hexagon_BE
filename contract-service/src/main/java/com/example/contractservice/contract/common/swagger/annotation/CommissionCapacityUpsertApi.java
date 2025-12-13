package com.example.contractservice.contract.common.swagger.annotation;

import io.swagger.v3.oas.annotations.Operation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "의뢰글 수용 인원 데이터 추가", description = "특정 의뢰글에 해당하는 최대 지원 인원, 현재 지원 인원, 최대 선정 인원, 현재 선정 인원을 추가")
public @interface CommissionCapacityUpsertApi {

}

