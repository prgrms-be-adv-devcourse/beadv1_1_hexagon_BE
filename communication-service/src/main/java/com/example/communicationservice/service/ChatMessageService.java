package com.example.communicationservice.service;

import com.example.communicationservice.client.S3ServiceClient;
import com.example.communicationservice.client.dto.input.FileDownloadUrlGenerateInput;
import com.example.communicationservice.client.dto.output.FileDownloadUrlListGenerateOutput;
import com.example.communicationservice.common.exception.ChatRoomException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.controller.dto.request.ChatMessageSendRequest;
import com.example.communicationservice.controller.dto.response.ChatMessageListReadResponse;
import com.example.communicationservice.controller.dto.response.ChatMessageReadResponse;
import com.example.communicationservice.controller.dto.response.ChatMessageSendResponse;
import com.example.communicationservice.controller.dto.response.PageInfo;
import com.example.communicationservice.entity.ChatMessage;
import com.example.communicationservice.entity.ChatRoom;
import com.example.communicationservice.mapper.ChatMapper;
import com.example.communicationservice.repository.ChatMessageRepository;
import com.example.communicationservice.repository.ChatRoomRepository;
import com.example.communicationservice.type.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final S3ServiceClient s3ServiceClient;

    /**
     * 특정 채팅방의 메시지 목록을 페이징하여 조회합니다.
     * @param roomId 조회할 채팅방 아이디
     * @param currentMemberCode 현재 로그인한 회원의 코드
     * @param pageable 페이징 요청 정보
     * @return 해당 채팅방의 메시지 목록
     */
    public ChatMessageListReadResponse findMessagesByRoomId(String roomId, String currentMemberCode, Pageable pageable) {
        // 해당 채팅방이 존재하는지 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ChatRoomException(ResponseDtoStatus.CHATROOM_NOT_FOUND));

        // 현재 로그인한 회원이 해당 채팅방의 참여자인지 확인
        if (!chatRoom.getMemberCodes().contains(currentMemberCode)) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_FORBIDDEN);
        }

        Page<ChatMessage> messagePage = chatMessageRepository.findAllByRoomId(roomId, pageable);

        // 엔티티 -> DTO 변환
        List<ChatMessageReadResponse> messages = messagePage.getContent().stream()
            .map(ChatMapper::toReadResponse)
            .toList();

        // Page 정보 추출 및 DTO 생성
        PageInfo pageInfo = ChatMapper.toPageInfo(messagePage);

        return new ChatMessageListReadResponse(messages, pageInfo);
    }

    /**
     * 채팅 메시지 전송을 처리합니다. <br>
     * - 채팅 메시지를 DB에 저장하고 <br>
     * - 파일이 포함된 경우 pre-signed download URL을 생성한 뒤 <br>
     * - 브로드캐스트할 응답 형태로 변환합니다. <br>
     * @param request 채팅 메시지 전송 요청
     * @return 수신할 메시지
     */
    public ChatMessageSendResponse sendMessage(ChatMessageSendRequest request) {
        // 해당 채팅방이 존재하는지 확인
        ChatRoom chatRoom = chatRoomRepository.findById(request.roomId())
            .orElseThrow(() -> new ChatRoomException(ResponseDtoStatus.CHATROOM_NOT_FOUND));

        // 메시지 송신자가 해당 채팅방의 참여자인지 확인
        if (!chatRoom.getMemberCodes().contains(request.senderCode())) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_FORBIDDEN);
        }

        // 메시지 생성 및 타입별 유효성 자동 검증
        ChatMessage chatMessage = ChatMapper.toEntity(request);

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        // 채팅방의 updatedAt 갱신
        chatRoom.setUpdatedAt(Instant.now());
        chatRoomRepository.save(chatRoom);

        // pre-signed download URL 생성
        FileDownloadUrlListGenerateOutput output = generateDownloadUrl(request);

        return ChatMapper.toSendResponse(savedChatMessage, output);
    }

    /**
     * s3 service와 통신해 pre-signed download URL을 생성합니다.
     * @param request 저장할 채팅 메시지 전송 요청
     * @return pre-signed download URL 생성 응답
     */
    private FileDownloadUrlListGenerateOutput generateDownloadUrl(ChatMessageSendRequest request) {
        // 파일이 포함되지 않은 메시지인 경우 early return
        if (MessageType.FILE != request.type() && MessageType.MIXED != request.type()) {
            return null;
        }

        // feign client 요청 dto 생성
        String key = request.file().key();
        FileDownloadUrlGenerateInput input = new FileDownloadUrlGenerateInput(List.of(key));

        // S3 service에 pre-signed download URL 발급 요청
        return s3ServiceClient.generateDownloadUrl(input).data();
    }

}
