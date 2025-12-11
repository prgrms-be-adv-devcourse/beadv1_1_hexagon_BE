package com.example.searchservice.common.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "페이지네이션 정보(page, size)")
public record Pagination(

        @Schema(description = "페이지 번호(0부터 시작)", example = "0")
        @Min(0)
        Integer page,

        @Schema(description = "페이지 크기(1 ~ 30)", example = "20")
        @Min(1)
        @Max(30)
        Integer size
) {

    public Pagination {
        page = (page == null) ? 0 : page;
        size = (size == null) ? 20 : size;
    }
}
