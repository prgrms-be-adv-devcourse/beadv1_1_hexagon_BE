package com.example.communicationservice.controller.dto.response;

public record PageInfo(
    int page, // 현재 페이지 번호(0-based)
    int size, // 페이지당 항목 수
    long totalElements, // 전체 항목 수
    int totalPages // 전체 페이지수
) {
}
