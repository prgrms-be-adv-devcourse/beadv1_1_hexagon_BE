package com.example.searchservice.commission.controller.swagger;

import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.commission.vo.OpenStatus;
import com.example.searchservice.common.vo.Pagination;
import com.example.searchservice.common.vo.SearchScope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.vo.PaymentType;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

@Tag(name = "Commission Search API", description = "의뢰글 검색 / 검색 키워드 추천 API")
@Validated
public interface CommissionControllerSwagger {

    @Operation(summary = "의뢰글 검색", description = "조건(검색어, 태그, 급여, 기간 등)을 기반으로 의뢰글을 검색합니다.")
    @Parameters({
            @Parameter(name = "query", description = "검색어", required = false),
            @Parameter(name = "scope", description = "검색 범위 (all(default) | title | content)"),
            @Parameter(name = "tags", description = "태그 목록", required = false),
            @Parameter(name = "payment-type", description = "급여 지급 방식 (MONTHLY | PER_JOB)", required = false),
            @Parameter(name = "min-pay", description = "최소 급여", required = false),
            @Parameter(name = "started-at", description = "프로젝트 시작일 (yyyy-MM-dd)", required = false),
            @Parameter(name = "ended-at", description = "프로젝트 종료일 (yyyy-MM-dd)", required = false),
            @Parameter(name = "open-status", description = "마감 여부(all | open(default) | closed"),
            @Parameter(name = "page", description = "페이지 번호(0부터 시작)", required = false),
            @Parameter(name = "size", description = "페이지 크기(1 ~ 30)", required = false)
    })
    ResponseDto<Page<CommissionResponseDto>> search(
            String query,
            SearchScope scope,
            List<String> tags,
            PaymentType paymentType,
            Long minPay,
            LocalDate startedAt,
            LocalDate endedAt,
            OpenStatus openStatus,
            @Min(0) int page,
            @Max(30) int size
    );

    @Operation(summary = "의뢰글 추천 검색 키워드", description = "입력한 접두어(prefix)를 기반으로 추천 검색 키워드를 반환합니다.")
    @Parameters({
            @Parameter(name = "query", description = "검색어 접두어", required = true),
            @Parameter(name = "size", description = "보여줄 추천 검색 키워드 개수", required = false)
    })
    ResponseDto<List<String>> suggest(
            String query,
            int size
    );
}
