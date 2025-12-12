package com.example.communicationservice.mapper;

import com.example.communicationservice.client.dto.output.FileDownloadUrlListGenerateOutput;
import com.example.communicationservice.controller.dto.request.ChatMessageSendRequest;
import com.example.communicationservice.controller.dto.response.ChatMessageReadResponse;
import com.example.communicationservice.controller.dto.response.ChatMessageSendResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomCreateResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomReadResponse;
import com.example.communicationservice.entity.ChatMessage;
import com.example.communicationservice.entity.ChatRoom;

// dto <-> entity 또는 dto <-> dto 변환 로직 전담
public abstract class ChatMapper {

    private ChatMapper() {} // 인스턴스화 방지

    // ------------------ ChatMessage ------------------

    public static ChatMessage toEntity(ChatMessageSendRequest request) {
        return ChatMessage.builder()
            .roomId(request.roomId())
            .senderCode(request.senderCode())
            .type(request.type())
            .text(request.text())
            .file(request.file() != null ? FileMapper.toEntity(request.file()) : null)
            .build();
    }

    public static ChatMessageReadResponse toReadResponse(ChatMessage message) {
        return new ChatMessageReadResponse(
            message.getId(),
            message.getSenderCode(),
            message.getType(),
            message.getText(),
            FileMapper.from(message.getFile()),
            message.getSentAt()
        );
    }

    public static ChatMessageSendResponse toSendResponse(
        ChatMessage message,
        FileDownloadUrlListGenerateOutput output
    ) {
        return new ChatMessageSendResponse(
            message.getId(),
            message.getRoomId(),
            message.getSenderCode(),
            message.getType(),
            message.getText(),
            output != null ? FileMapper.from(output.urls().get(0)) : null,
            message.getSentAt()
        );
    }

    // ------------------ ChatRoom ------------------

    public static ChatRoomCreateResponse toCreateResponse(ChatRoom chatRoom) {
        return new ChatRoomCreateResponse(chatRoom.getId());
    }

    public static ChatRoomReadResponse toReadResponse(ChatRoom chatRoom) {
        return new ChatRoomReadResponse(
            chatRoom.getId(),
            chatRoom.getName(),
            chatRoom.getUpdatedAt()
        );
    }

}
