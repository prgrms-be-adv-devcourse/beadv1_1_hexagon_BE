package com.example.communicationservice.controller.dto.response;

import org.springframework.data.domain.Page;

public record PageInfo(
    int page, // 현재 페이지 번호(0-based)
    int size, // 페이지당 항목 수
    long totalElements, // 전체 항목 수
    int totalPages // 전체 페이지수
) {
    public static PageInfo from(Page<?> page) {
        return new PageInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
