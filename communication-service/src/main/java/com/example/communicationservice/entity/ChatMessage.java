package com.example.communicationservice.entity;

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

    private String content;

    @CreatedDate
    private Instant sentAt; // 전송된 시간

    @Builder
    private ChatMessage(String roomId, String senderCode, String content) {
        this.roomId = roomId;
        this.senderCode = senderCode;
        this.content = content;
    }

}
