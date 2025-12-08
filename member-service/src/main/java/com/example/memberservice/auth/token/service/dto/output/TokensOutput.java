package com.example.memberservice.auth.token.service.dto.output;

public record TokensOutput(
    String accessToken,
    String refreshToken
) {

}
