package com.example.communicationservice.controller;

import static com.example.communicationservice.common.response.ResponseDtoMapper.getErrorResponse;

import com.example.communicationservice.common.exception.ChatRoomException;
import com.example.communicationservice.controller.dto.request.ChatMessageSendRequest;
import com.example.communicationservice.controller.dto.response.ChatMessageSendResponse;
import com.example.communicationservice.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    /**
     * 메시지 수신 처리 <br>
     * 1. 채팅방 참여자 인가 <br>
     * 2. DB 저장 <br>
     * 3. 해당 토픽 구독자에게 메시지 전송 <br>
     */
    @MessageMapping("chat.send")
    public void handleChatMessage(@Valid @Payload ChatMessageSendRequest request) {
        ChatMessageSendResponse response = chatMessageService.sendMessage(request);

        String destinationPrefix = "/queue/room/";
        messagingTemplate.convertAndSend(destinationPrefix + request.roomId(), response);
    }

    // WebSocket 전용 예외 처리
    @MessageExceptionHandler(ChatRoomException.class)
    @SendToUser("/queue/errors")
    public ResponseDto<Empty> handleChatRoomException(ChatRoomException ex) {
        return getErrorResponse(ex.getStatus());
    }

}
