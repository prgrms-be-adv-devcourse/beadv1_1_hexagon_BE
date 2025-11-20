package com.example.communicationservice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Document(collection = "chat_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    private String id; // MongoDB의 PK, ObjectId와 매핑

    private String name;

    private List<String> memberCodes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    @Setter
    private Instant updatedAt;

    @Builder
    private ChatRoom(String name, List<String> memberCodes) {
        this.name = name;
        this.memberCodes = memberCodes;
    }

}
