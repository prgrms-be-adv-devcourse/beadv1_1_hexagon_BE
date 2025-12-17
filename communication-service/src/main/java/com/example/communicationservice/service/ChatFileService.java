package com.example.communicationservice.service;

import com.example.communicationservice.client.S3ServiceClient;
import com.example.communicationservice.client.dto.input.FileUploadUrlGenerateInput;
import com.example.communicationservice.client.dto.output.FileUploadUrlGenerateOutput;
import com.example.communicationservice.common.exception.ChatRoomException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.controller.dto.request.ChatFileUploadUrlGenerateRequest;
import com.example.communicationservice.controller.dto.response.ChatFileUploadUrlGenerateResponse;
import com.example.communicationservice.entity.ChatRoom;
import com.example.communicationservice.mapper.FileMapper;
import com.example.communicationservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.vo.ServiceName;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatFileService {

    private final ChatRoomRepository chatRoomRepository;
    private final S3ServiceClient s3ServiceClient;

    /**
     * 채팅방에 파일을 업로드하는 데 필요한 pre-signed upload URL을 생성합니다.
     * @param roomId 파일을 업로드할 채팅방 아이디
     * @param senderCode 파일을 업로드할 회원의 코드
     * @param request pre-signed upload URL 생성 요청
     * @return pre-signed upload URL 생성 응답
     */
    public ChatFileUploadUrlGenerateResponse generateUploadUrl(
        String roomId,
        String senderCode,
        ChatFileUploadUrlGenerateRequest request
    ) {
        // 해당 채팅방이 존재하는지 확인
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ChatRoomException(ResponseDtoStatus.CHATROOM_NOT_FOUND));

        // 파일을 업로드하려는 회원이 해당 채팅방의 참여자인지 확인
        if (!chatRoom.getMemberCodes().contains(senderCode)) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_FORBIDDEN);
        }

        // feign client 요청 dto 생성
        FileUploadUrlGenerateInput input = new FileUploadUrlGenerateInput(
            ServiceName.CHATS,
            request.fileName(),
            request.contentType()
        );

        // S3 service에 pre-signed upload URL 발급 요청
        FileUploadUrlGenerateOutput output = s3ServiceClient.generateUploadUrl(input).data();

        return FileMapper.from(output);
    }

}
