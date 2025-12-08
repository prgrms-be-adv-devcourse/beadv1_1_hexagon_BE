package com.example.communicationservice.controller;

import com.example.communicationservice.controller.api.ChatRoomControllerApi;
import com.example.communicationservice.controller.dto.request.ChatRoomCreateRequest;
import com.example.communicationservice.controller.dto.response.ChatMessageListReadResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomCreateResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomListReadResponse;
import com.example.communicationservice.service.ChatMessageService;
import com.example.communicationservice.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
public class ChatRoomController implements ChatRoomControllerApi {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    // 채팅방 생성 API
    @PostMapping
    @Override
    public ResponseEntity<ResponseDto<ChatRoomCreateResponse>> createRoom(
        @Valid @RequestBody ChatRoomCreateRequest request,
        @RequestHeader(name = "X-CODE") String currentMemberCode
    ) {
        ChatRoomCreateResponse response = chatRoomService.createChatRoom(request, currentMemberCode);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDto.success(response));
    }

    // 채팅방 목록 조회 API
    @GetMapping
    @Override
    public ResponseEntity<ResponseDto<ChatRoomListReadResponse>> findRooms(
        @PageableDefault(
            size = 10,
            sort = "updatedAt",
            direction = Sort.Direction.DESC
        )
        Pageable pageable,
        @RequestHeader(name = "X-CODE") String currentMemberCode
    ) {
        ChatRoomListReadResponse response = chatRoomService.findAllChatRooms(currentMemberCode, pageable);

        return ResponseEntity
            .ok()
            .body(ResponseDto.success(response));
    }

    // 채팅방 메시지 목록 조회 API
    @GetMapping("/{room-id}/messages")
    @Override
    public ResponseEntity<ResponseDto<ChatMessageListReadResponse>> findMessages(
        @PathVariable(name = "room-id") String roomId,
        @RequestHeader(name = "X-CODE") String currentMemberCode,
        @PageableDefault(
            size = 50,
            sort = "sentAt",
            direction = Sort.Direction.ASC
        )
        Pageable pageable
    ) {
        ChatMessageListReadResponse response = chatMessageService.findMessagesByRoomId(roomId, currentMemberCode, pageable);

        return ResponseEntity
            .ok()
            .body(ResponseDto.success(response));
    }

}
