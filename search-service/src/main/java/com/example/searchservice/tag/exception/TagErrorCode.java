package com.example.searchservice.tag.exception;

import com.example.searchservice.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagErrorCode implements ErrorCode {
    TAG_FETCH_FAILED(5001, 500, "태그 불러오기 실패"),
    TAG_ALIAS_LOAD_FAILED(5002, 500, "태그 별칭 로드 실패"),
    TAG_SUGGEST_FAILED(5003, 500, "태그 추천 실패");

    private final int code;
    private final int httpStatus;
    private final String message;
}
