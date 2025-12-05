package com.example.communicationservice.type;

import com.example.communicationservice.common.exception.ChatMessageException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.entity.ChatMessage;

public enum MessageType {

    // 메시지 타입 1: 텍스트
    TEXT {
        @Override
        public void validate(ChatMessage message) {
            // 필수 필드 존재 여부 확인
            if (message.getText() == null || message.getText().isBlank()) {
                throw new ChatMessageException(ResponseDtoStatus.TEXT_MISSING);
            }

            // 필수 필드 상세 값 검증
            if (message.getText().length() > TEXT_MAX_LENGTH) {
                throw new ChatMessageException(ResponseDtoStatus.TOO_LONG_TEXT);
            }

            // 금지 필드 존재 여부 확인
            if (message.getFile() != null) {
                throw new ChatMessageException(ResponseDtoStatus.FILE_NOT_ALLOWED);
            }
        }
    },

    // 메시지 타입 2: 파일
    FILE {
        @Override
        public void validate(ChatMessage message) {
            // 필수 필드 존재 여부 확인
            if (message.getFile() == null) {
                throw new ChatMessageException(ResponseDtoStatus.FILE_MISSING);
            }

            // 필수 필드 존재 여부 확인
            if (message.getFile().getKey() == null) {
                throw new ChatMessageException(ResponseDtoStatus.FILE_KEY_MISSING);
            }

            // 금지 필드 존재 여부 확인
            if (message.getText() != null && !message.getText().isBlank()) {
                throw new ChatMessageException(ResponseDtoStatus.TEXT_NOT_ALLOWED);
            }
        }
    },

    // 메시지 타입 3: 텍스트 + 파일
    MIXED {
        @Override
        public void validate(ChatMessage message) {
            // 필수 필드 존재 여부 확인
            if (message.getText() == null || message.getText().isBlank()) {
                throw new ChatMessageException(ResponseDtoStatus.TEXT_MISSING);
            }

            // 필수 필드 존재 여부 확인
            if (message.getFile() == null) {
                throw new ChatMessageException(ResponseDtoStatus.FILE_MISSING);
            }

            // 필수 필드 존재 여부 확인
            if (message.getFile().getKey() == null) {
                throw new ChatMessageException(ResponseDtoStatus.FILE_KEY_MISSING);
            }

            // 필수 필드 상세 값 검증
            if (message.getText().length() > TEXT_MAX_LENGTH) {
                throw new ChatMessageException(ResponseDtoStatus.TOO_LONG_TEXT);
            }
        }
    };

    public abstract void validate(ChatMessage message);

    // 텍스트 최대 길이
    private static final int TEXT_MAX_LENGTH = 1000;

}
