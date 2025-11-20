package com.example.communicationservice.client.dto;

import java.util.List;

public record MemberExistOutput(
    List<String> exists, // 유효한 회원 코드 목록
    List<String> notExists // 유효하지 않은 회원 코드 목록
) {
}
