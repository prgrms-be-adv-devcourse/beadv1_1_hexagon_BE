package com.example.searchservice.commission.controller.swagger;

import com.example.searchservice.commission.common.PaymentType;
import com.example.searchservice.commission.dto.CommissionResponseDto;
import com.example.searchservice.common.response.BaseResponse;
import com.example.searchservice.common.vo.SearchScope;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Commission Search API", description = "의뢰글 검색 / 검색 키워드 추천 API")
public interface CommissionSearchControllerSwagger {

    @Operation(summary = "의뢰글 검색", description = "조건(검색어, 태그, 급여, 기간 등)을 기반으로 의뢰글을 검색합니다.")
    @Parameters({
            @Parameter(name = "query", description = "검색어", required = false),
            @Parameter(name = "scope", description = "검색 범위 (all(default) | title | content)"),
            @Parameter(name = "tags", description = "태그 목록", required = false),
            @Parameter(name = "payment-type", description = "급여 지급 방식 (MONTHLY | ONE_TIME)", required = false),
            @Parameter(name = "min-pay", description = "최소 급여", required = false),
            @Parameter(name = "started-at", description = "프로젝트 시작일 (yyyy-MM-dd)", required = false),
            @Parameter(name = "ended-at", description = "프로젝트 종료일 (yyyy-MM-dd)", required = false),
            @Parameter(name = "page", description = "페이지 번호", required = false),
            @Parameter(name = "size", description = "페이지 크기", required = false)
    })
    BaseResponse<Page<CommissionResponseDto>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "all") SearchScope scope,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(name = "payment-type", required = false) PaymentType paymentType,
            @RequestParam(name = "min-pay", required = false) Long minPay,
            @RequestParam(name = "started-at", required = false) LocalDate startedAt,
            @RequestParam(name = "ended-at", required = false) LocalDate endedAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(summary = "의뢰글 추천 검색 키워드", description = "입력한 접두어(prefix)를 기반으로 추천 검색 키워드를 반환합니다.")
    @Parameters({
            @Parameter(name = "query", description = "검색어 접두어", required = true),
            @Parameter(name = "size", description = "보여줄 추천 검색 키워드 개수", required = false)
    })
    BaseResponse<CommissionResponseDto> suggest(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int size
    );
}