package com.example.communicationservice.service;

import com.example.communicationservice.client.MemberServiceClient;
import com.example.communicationservice.client.dto.MemberExistOutput;
import com.example.communicationservice.common.exception.ChatRoomException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.controller.dto.response.ChatRoomCreateResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomListReadResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomReadResponse;
import com.example.communicationservice.controller.dto.response.PageInfo;
import com.example.communicationservice.entity.ChatRoom;
import com.example.communicationservice.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberServiceClient memberServiceClient;

    /**
     * 새로운 채팅방을 생성하고 저장합니다.
     * @param name 채팅방 이름
     * @param memberCodes 채팅방 참여자들의 코드 목록
     * @param currentMemberCode 현재 로그인한 회원의 코드
     * @return 생성된 채팅방 아이디
     */
    public ChatRoomCreateResponse createChatRoom(String name, List<String> memberCodes, String currentMemberCode) {
        // 1:1 채팅인지 확인
        if (memberCodes.stream().distinct().count() != 2) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_INVALID_MEMBER_COUNT);
        }

        // 현재 로그인한 회원이 참여자 코드 목록에 포함되어 있는지 확인
        if (!memberCodes.contains(currentMemberCode)) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_NOT_INCLUDE_SELF);
        }

        MemberExistOutput result = memberServiceClient.getMemberExistences(memberCodes).data();

        // memberCodes의 모든 참여자 코드가 유효한지 확인
        if (!result.notExists().isEmpty()) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_INVALID_MEMBER);
        }

        // 이미 생성된 채팅방이 있는지 확인
        if (chatRoomRepository.existsByMemberCodes(memberCodes, memberCodes.size())) {
            throw new ChatRoomException(ResponseDtoStatus.CHATROOM_ALREADY_EXISTS);
        }

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
            .name(name)
            .memberCodes(memberCodes)
            .build();

        // 채팅방 저장
        ChatRoom createdChatRoom = chatRoomRepository.save(chatRoom);

        return ChatRoomCreateResponse.from(createdChatRoom);
    }

    /**
     * 채팅방 목록을 페이징하여 조회합니다.
     * @param currentMemberCode 현재 로그인한 회원의 코드
     * @param pageable 페이징 요청 정보
     * @return 현재 로그인한 회원이 속한 채팅방 목록
     */
    public ChatRoomListReadResponse findAllChatRooms(String currentMemberCode, Pageable pageable) {
        Page<ChatRoom> chatRoomPage = chatRoomRepository.findAllByMemberCode(currentMemberCode, pageable);

        // 엔티티 -> DTO 변환
        List<ChatRoomReadResponse> chatRooms = chatRoomPage.getContent().stream()
            .map(ChatRoomReadResponse::from)
            .toList();

        // Page 정보 추출 및 DTO 생성
        PageInfo pageInfo = PageInfo.from(chatRoomPage);

        return new ChatRoomListReadResponse(chatRooms, pageInfo);
    }

}
