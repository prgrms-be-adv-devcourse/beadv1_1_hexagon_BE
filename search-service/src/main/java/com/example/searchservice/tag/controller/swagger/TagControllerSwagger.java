package com.example.searchservice.tag.controller.swagger;

import com.example.searchservice.common.response.BaseResponse;
import com.example.searchservice.tag.dto.TagResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@Tag(name = "Tag Suggest API", description = "Tag 검색어 추천 API")
@Validated
public interface TagControllerSwagger {

    @Operation(summary = "Tag 추천 검색어", description = "입력한 접두어(prefix)를 기반으로 추천 검색어를 반환합니다.")
    @Parameters({
            @Parameter(name = "query", description = "검색어 접두어", required = true),
            @Parameter(name = "size", description = "보여줄 추천 검색어 개수", required = false)
    })
    BaseResponse<List<TagResponseDto>> suggest(
            @NotBlank String query,
            @Min(10) @Max(50) int size
    );
}
