package com.example.communicationservice.entity;

import com.example.communicationservice.common.exception.ChatMessageException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.type.MessageType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Document(collection = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id; // MongoDB의 PK, ObjectId와 매핑

    private String roomId;

    private String senderCode;

    private MessageType type;

    private String text;

    private File file;

    @CreatedDate
    private Instant sentAt; // 전송된 시간

    @Builder
    private ChatMessage(
        String roomId,
        String senderCode,
        MessageType type,
        String text,
        File file
    ) {
        // 메시지 타입 null 체크
        if (type == null) {
            throw new ChatMessageException(ResponseDtoStatus.MESSAGE_TYPE_MISSING);
        }

        this.roomId = roomId;
        this.senderCode = senderCode;
        this.type = type;
        this.text = text;
        this.file = file;

        // 타입별 유효성 검증
        type.validate(this);
    }

}
