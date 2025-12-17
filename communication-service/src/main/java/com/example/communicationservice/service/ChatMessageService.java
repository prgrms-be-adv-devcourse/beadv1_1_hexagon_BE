package com.example.communicationservice.service;

import com.example.communicationservice.client.S3ServiceClient;
import com.example.communicationservice.client.dto.input.FileDownloadUrlGenerateInput;
import com.example.communicationservice.client.dto.output.FileDownloadUrlGenerateOutput;
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
import com.example.communicationservice.entity.File;
import com.example.communicationservice.mapper.ChatMapper;
import com.example.communicationservice.mapper.PageMapper;
import com.example.communicationservice.repository.ChatMessageRepository;
import com.example.communicationservice.repository.ChatRoomRepository;
import com.example.communicationservice.type.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        // 파일이 포함된 메시지에서 key 추출 후 리스트에 저장
        List<String> keys = extractFileKeys(messagePage.getContent());

        // key: 파일 key, value: pre-signed download URL 생성 응답
        Map<String, FileDownloadUrlGenerateOutput> keyToDownloadUrl = generateDownloadUrlMap(keys);

        // 엔티티 -> dto 변환
        List<ChatMessageReadResponse> messages = messagePage.getContent().stream()
            .map(message -> ChatMapper.toReadResponse(message, keyToDownloadUrl))
            .toList();

        // Page 정보 추출 및 dto 생성
        PageInfo pageInfo = PageMapper.toPageInfo(messagePage);

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

        FileDownloadUrlGenerateOutput output = null;

        // pre-signed download URL 생성
        if (hasFile(request.type())) {
            output = generateSingleDownloadUrl(request.file().key()).orElse(null);
        }

        return ChatMapper.toSendResponse(savedChatMessage, output);
    }

    private boolean hasFile(MessageType type) {
        return MessageType.FILE == type || MessageType.MIXED == type;
    }

    /**
     * s3 service와 통신해 단일 파일에 대한 pre-signed download URL을 생성합니다.
     * @param key pre-signed download URL을 생성할 파일 key
     * @return pre-signed download URL 생성 응답
     */
    private Optional<FileDownloadUrlGenerateOutput> generateSingleDownloadUrl(String key) {
        // feign client 요청 dto 생성
        FileDownloadUrlGenerateInput input = new FileDownloadUrlGenerateInput(List.of(key));

        // S3 service에 pre-signed download URL 발급 요청
        FileDownloadUrlListGenerateOutput output = s3ServiceClient.generateDownloadUrl(input).data();

        return output.urls().stream().findFirst();
    }

    /**
     * 채팅 메시지 목록에서 파일이 포함된 메시지의 파일 key를 추출합니다.
     * @param messages 채팅 메시지 목록
     * @return 파일이 포함된 메시지에서 추출한 파일 key 목록
     */
    private List<String> extractFileKeys(List<ChatMessage> messages) {
        return messages.stream()
            .filter(message -> hasFile(message.getType())) // 파일이 포함된 메시지만 필터링
            .map(ChatMessage::getFile)
            .filter(Objects::nonNull)
            .map(File::getKey) // key 추출
            .distinct() // 동일 파일 중복 방지
            .toList();
    }

    /**
     * S3 service와 통신해 다중 파일에 대한 pre-signed download URL 매핑 정보를 생성합니다.
     * @param keys pre-signed download URL을 생성할 파일 key 목록
     * @return 파일 key 기반 pre-signed download URL 매핑 정보
     */
    private Map<String, FileDownloadUrlGenerateOutput> generateDownloadUrlMap(List<String> keys) {
        if (keys.isEmpty()) {
            return Map.of();
        }

        // feign client 요청 dto 생성
        FileDownloadUrlGenerateInput input = new FileDownloadUrlGenerateInput(keys);

        // S3 service에 pre-signed download URL 발급 요청
        FileDownloadUrlListGenerateOutput output = s3ServiceClient.generateDownloadUrl(input).data();

        // 파일 key에 pre-signed download URL 생성 응답 매핑
        return output.urls().stream()
            .collect(
                Collectors.toMap(
                    FileDownloadUrlGenerateOutput::key, // key
                    Function.identity() // value // 입력으로 받은 FileDownloadUrlGenerateOutput을 그대로 반환
                )
            );
    }

}
