package com.example.communicationservice.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatRoomCreateRequest(

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    @Size(max = 50, message = "채팅방 이름은 최대 50자까지 가능합니다.")
    String name,

    @NotNull(message = "참여자 목록은 필수입니다.")
    List<@NotBlank(message = "참여자 코드는 비어 있을 수 없습니다.") String> memberCodes

) {
}
