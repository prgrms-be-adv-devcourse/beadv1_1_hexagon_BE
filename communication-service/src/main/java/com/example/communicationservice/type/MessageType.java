package com.example.communicationservice.type;

import com.example.communicationservice.common.exception.ChatMessageException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.entity.ChatMessage;

import java.util.function.Consumer;

public enum MessageType {

    // 메시지 타입 1: 텍스트
    TEXT(message -> {
        requireText(message);
        validateTextLength(message);
        forbidFile(message);
    }),

    // 메시지 타입 2: 파일
    FILE(message -> {
        requireFile(message);
        requireFileKey(message);
        forbidText(message);
    }),

    // 메시지 타입 3: 텍스트 + 파일
    MIXED(message -> {
        requireText(message);
        validateTextLength(message);
        requireFile(message);
        requireFileKey(message);
    });

    private final Consumer<ChatMessage> validator;

    MessageType(Consumer<ChatMessage> validator) {
        this.validator = validator;
    }

    public void validate(ChatMessage message) {
        validator.accept(message);
    }

    // -----------------------------------------------------
    //               공통 검증 로직 모듈화 영역
    // -----------------------------------------------------

    // 텍스트가 존재하는지 검증
    private static void requireText(ChatMessage message) {
        if (message.getText() == null || message.getText().isBlank()) {
            throw new ChatMessageException(ResponseDtoStatus.TEXT_MISSING);
        }
    }

    // 텍스트 길이가 최대 길이를 넘지 않는지 검증
    private static void validateTextLength(ChatMessage message) {
        if (message.getText().length() > TEXT_MAX_LENGTH) {
            throw new ChatMessageException(ResponseDtoStatus.TOO_LONG_TEXT);
        }
    }

    // 파일이 존재하지 않는지 검증
    private static void forbidFile(ChatMessage message) {
        if (message.getFile() != null) {
            throw new ChatMessageException(ResponseDtoStatus.FILE_NOT_ALLOWED);
        }
    }

    // 파일이 존재하는지 검증
    private static void requireFile(ChatMessage message) {
        if (message.getFile() == null) {
            throw new ChatMessageException(ResponseDtoStatus.FILE_MISSING);
        }
    }

    // 파일 키가 존재하는지 검증
    private static void requireFileKey(ChatMessage message) {
        if (message.getFile().getKey() == null) {
            throw new ChatMessageException(ResponseDtoStatus.FILE_KEY_MISSING);
        }
    }

    // 텍스트가 존재하지 않는지 검증
    private static void forbidText(ChatMessage message) {
        if (message.getText() != null && !message.getText().isBlank()) {
            throw new ChatMessageException(ResponseDtoStatus.TEXT_NOT_ALLOWED);
        }
    }

    private static final int TEXT_MAX_LENGTH = 1000;

}
