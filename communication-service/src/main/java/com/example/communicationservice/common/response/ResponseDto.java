package com.example.communicationservice.common.response;

import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"code", "httpStatus", "message", "data"})
public class ResponseDto<T> {

    private final int code;

    @JsonProperty("httpStatus")
    private final int httpStatusCode;

    private final String message;

    private final T data;

    public static ResponseDto<Empty> success() {
        return new ResponseDto<>(ResponseDtoStatus.SUCCESS, Empty.getInstance());
    }

    public static <T> ResponseDto<T> success(T result) {
        return new ResponseDto<>(ResponseDtoStatus.SUCCESS, result);
    }

    public static ResponseDto<Empty> error(ResponseDtoStatus status) {
        return new ResponseDto<>(status, Empty.getInstance());
    }

    public static <T> ResponseDto<T> error(ResponseDtoStatus status, T data) {
        return new ResponseDto<>(status, data);
    }

    private ResponseDto(ResponseDtoStatus status, T data) {
        this.code = status.getCode();
        this.httpStatusCode = status.getHttpStatusCode();
        this.message = status.getMessage();
        this.data = data;
    }

}
