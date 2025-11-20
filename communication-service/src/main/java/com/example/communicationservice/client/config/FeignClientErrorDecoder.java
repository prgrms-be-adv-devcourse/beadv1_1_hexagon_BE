package com.example.communicationservice.client.config;

import com.example.communicationservice.client.exception.FeignClientException;
import com.example.communicationservice.common.response.ResponseDto;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class FeignClientErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {

        try {
            // body가 있는 경우 JSON 파싱 시도
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));

                try {
                    ResponseDto<?> errorResponse = objectMapper.readValue(body, new TypeReference<>() {});

                    int httpStatusCode = errorResponse.getHttpStatusCode();

                    return switch (httpStatusCode) {
                        case 400 -> new FeignClientException(ResponseDtoStatus.FEIGN_BAD_REQUEST);
                        case 401 -> new FeignClientException(ResponseDtoStatus.FEIGN_UNAUTHORIZED);
                        case 403 -> new FeignClientException(ResponseDtoStatus.FEIGN_FORBIDDEN);
                        case 404 -> new FeignClientException(ResponseDtoStatus.FEIGN_NOT_FOUND);
                        case 500 -> new FeignClientException(ResponseDtoStatus.FEIGN_INTERNAL_SERVER_ERROR);
                        default -> new FeignClientException(ResponseDtoStatus.FEIGN_UNKNOWN_ERROR);
                    };

                } catch (JsonProcessingException e) {
                    // JSON 파싱 실패 시 알 수 없는 오류로 처리
                    return new FeignClientException(ResponseDtoStatus.FEIGN_UNKNOWN_ERROR);
                }
            }

            // body가 없는 경우 통신 문제로 처리
            return new FeignClientException(ResponseDtoStatus.FEIGN_COMMUNICATION_ERROR);

        } catch (IOException e) {
            // body를 읽을 수 없는 경우 통신 문제로 처리
            return new FeignClientException(ResponseDtoStatus.FEIGN_COMMUNICATION_ERROR);
        }
    }

}
