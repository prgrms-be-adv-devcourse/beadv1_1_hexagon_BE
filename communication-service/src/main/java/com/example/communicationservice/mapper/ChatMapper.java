package com.example.communicationservice.mapper;

import com.example.communicationservice.client.dto.output.FileDownloadUrlGenerateOutput;
import com.example.communicationservice.client.dto.output.FileDownloadUrlListGenerateOutput;
import com.example.communicationservice.client.dto.output.FileUploadUrlGenerateOutput;
import com.example.communicationservice.controller.dto.FileInfo;
import com.example.communicationservice.controller.dto.request.ChatFileSendRequest;
import com.example.communicationservice.controller.dto.request.ChatMessageSendRequest;
import com.example.communicationservice.controller.dto.response.*;
import com.example.communicationservice.entity.ChatMessage;
import com.example.communicationservice.entity.ChatRoom;
import com.example.communicationservice.entity.File;
import org.springframework.data.domain.Page;

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
            .file(request.file() != null ? toEntity(request.file()) : null)
            .build();
    }

    public static ChatMessageReadResponse toReadResponse(ChatMessage message) {
        return new ChatMessageReadResponse(
            message.getId(),
            message.getSenderCode(),
            message.getType(),
            message.getText(),
            from(message.getFile()),
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
            output != null ? from(output.urls().get(0)) : null,
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

    // ------------------ Paging ------------------

    public static PageInfo toPageInfo(Page<?> page) {
        return new PageInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext()
        );
    }

    // ------------------ File ------------------

    public static FileInfo from(File file) {
        if (file == null) {
            return null;
        }

        return new FileInfo(file.getKey());
    }

    public static File toEntity(ChatFileSendRequest request) {
        if (request == null) {
            return null;
        }

        return File.builder()
            .key(request.key())
            .build();
    }

    public static ChatFileUploadUrlGenerateResponse from(FileUploadUrlGenerateOutput output) {
        return new ChatFileUploadUrlGenerateResponse(
            output.key(),
            output.queryString()
        );
    }

    public static ChatFileSendResponse from(FileDownloadUrlGenerateOutput output) {
        return new ChatFileSendResponse(
            output.key(),
            output.queryString(),
            output.fileType()
        );
    }

}
