package com.example.communicationservice.service;

import com.example.communicationservice.client.MemberServiceClient;
import com.example.communicationservice.client.dto.MemberExistOutput;
import com.example.communicationservice.common.exception.ChatRoomException;
import com.example.communicationservice.common.response.ResponseDto;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.controller.dto.response.ChatRoomCreateResponse;
import com.example.communicationservice.controller.dto.response.ChatRoomListReadResponse;
import com.example.communicationservice.controller.dto.response.PageInfo;
import com.example.communicationservice.entity.ChatRoom;
import com.example.communicationservice.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MemberServiceClient memberServiceClient;

    @Test
    void 채팅방_생성에_성공한다() {
        // given
        String roomName = "채팅방";
        String myCode = "USER_A";
        String partnerCode = "USER_B";
        String roomId = "generated-mongodb-id-123";
        List<String> memberCodes = List.of(myCode, partnerCode);

        MemberExistOutput mockOutput = new MemberExistOutput(memberCodes, List.of());

        given(memberServiceClient.getMemberExistences(anyList()))
            .willReturn(ResponseDto.success(mockOutput));

        given(chatRoomRepository.existsByMemberCodes(anyList(), anyInt()))
            .willReturn(false);

        ChatRoom savedChatRoom = ChatRoom.builder()
            .name(roomName)
            .memberCodes(memberCodes)
            .build();

        ReflectionTestUtils.setField(savedChatRoom, "id", roomId); // 리플렉션으로 아이디 추가

        given(chatRoomRepository.save(any(ChatRoom.class)))
            .willReturn(savedChatRoom);

        // when
        ChatRoomCreateResponse response = chatRoomService.createChatRoom(roomName, memberCodes, myCode);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(roomId);

        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void 채팅방_참여자_수가_2명이_아니면_채팅방_생성에_실패한다() {
        // given
        String myCode = "USER_A";
        List<String> memberCodes = List.of(myCode);

        // when & then
        ChatRoomException exception = assertThrows(ChatRoomException.class,
            () -> chatRoomService.createChatRoom("채팅방", memberCodes, myCode));

        assertThat(exception.getStatus()).isEqualTo(ResponseDtoStatus.CHATROOM_INVALID_MEMBER_COUNT);
    }

    @Test
    void 채팅방_생성_요청자가_채팅방_참여자_목록에_없으면_채팅방_생성에_실패한다() {
        // given
        String myCode = "USER_A";
        List<String> memberCodes = List.of("USER_B", "USER_C");

        // when & then
        ChatRoomException exception = assertThrows(ChatRoomException.class,
            () -> chatRoomService.createChatRoom("채팅방", memberCodes, myCode));

        assertThat(exception.getStatus()).isEqualTo(ResponseDtoStatus.CHATROOM_NOT_INCLUDE_SELF);
    }

    @Test
    void 채팅방_참여자_목록에_유효하지_않은_회원이_있으면_채팅방_생성에_실패한다() {
        // given
        String myCode = "USER_A";
        String unknownCode = "USER_UNKNOWN";
        List<String> memberCodes = List.of(myCode, unknownCode);

        MemberExistOutput mockOutput = new MemberExistOutput(List.of(myCode), List.of(unknownCode));

        given(memberServiceClient.getMemberExistences(anyList()))
            .willReturn(ResponseDto.success(mockOutput));

        // when & then
        ChatRoomException exception = assertThrows(ChatRoomException.class,
            () -> chatRoomService.createChatRoom("채팅방", memberCodes, myCode));

        assertThat(exception.getStatus()).isEqualTo(ResponseDtoStatus.CHATROOM_INVALID_MEMBER);
    }

    @Test
    void 채팅방_참여자_사이에_이미_채팅방이_존재하면_채팅방_생성에_실패한다() {
        // given
        List<String> memberCodes = List.of("USER_A", "USER_B");

        MemberExistOutput mockOutput = new MemberExistOutput(memberCodes, List.of());

        given(memberServiceClient.getMemberExistences(anyList()))
            .willReturn(ResponseDto.success(mockOutput));

        given(chatRoomRepository.existsByMemberCodes(anyList(), anyInt()))
            .willReturn(true);

        // when & then
        ChatRoomException exception = assertThrows(ChatRoomException.class,
            () -> chatRoomService.createChatRoom("채팅방", memberCodes, "USER_A"));

        assertThat(exception.getStatus()).isEqualTo(ResponseDtoStatus.CHATROOM_ALREADY_EXISTS);
    }

    @Test
    void 채팅방_목록을_페이징하여_조회한다() {
        // given
        String myCode = "USER_A";
        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지, 크기 5
        Instant now = Instant.now();

        ChatRoom room1 = ChatRoom.builder().name("채팅방1").memberCodes(List.of(myCode, "USER_B")).build();
        ReflectionTestUtils.setField(room1, "id", "room-1");
        ReflectionTestUtils.setField(room1, "updatedAt", now.minusSeconds(300));

        ChatRoom room2 = ChatRoom.builder().name("채팅방2").memberCodes(List.of(myCode, "USER_C")).build();
        ReflectionTestUtils.setField(room2, "id", "room-2");
        ReflectionTestUtils.setField(room2, "updatedAt", now.minusSeconds(600));

        List<ChatRoom> chatRoomList = List.of(room1, room2);

        Page<ChatRoom> mockPage = new PageImpl<>(
            chatRoomList, // 현재 페이지 컨텐츠
            pageable,     // 요청 Pageable
            10L           // 총 요소 개수
        );

        given(chatRoomRepository.findAllByMemberCode(eq(myCode), eq(pageable)))
            .willReturn(mockPage);

        // when
        ChatRoomListReadResponse response = chatRoomService.findAllChatRooms(myCode, pageable);

        // then
        verify(chatRoomRepository, times(1)).findAllByMemberCode(eq(myCode), eq(pageable));

        assertThat(response).isNotNull();
        assertThat(response.chatRooms()).hasSize(chatRoomList.size());
        assertThat(response.chatRooms().get(0).id()).isEqualTo(chatRoomList.get(0).getId());
        assertThat(response.chatRooms().get(1).id()).isEqualTo(chatRoomList.get(1).getId());

        PageInfo pageInfo = response.pageInfo();
        assertThat(pageInfo.page()).isEqualTo(pageable.getPageNumber());
        assertThat(pageInfo.size()).isEqualTo(pageable.getPageSize());
        assertThat(pageInfo.totalPages()).isEqualTo(2); // (총 10개 / 페이지 크기 5)
        assertThat(pageInfo.totalElements()).isEqualTo(mockPage.getTotalElements());
    }

    @Test
    void 조회된_채팅방이_없을_경우_빈_목록을_반환한다() {
        // given
        String myCode = "USER_D";
        Pageable pageable = PageRequest.of(0, 10);

        Page<ChatRoom> mockEmptyPage = new PageImpl<>(
            List.of(),  // 빈 컨텐츠
            pageable,
            0L          // 총 요소 개수
        );

        given(chatRoomRepository.findAllByMemberCode(anyString(), any(Pageable.class)))
            .willReturn(mockEmptyPage);

        // when
        ChatRoomListReadResponse response = chatRoomService.findAllChatRooms(myCode, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.chatRooms()).isEmpty();

        PageInfo pageInfo = response.pageInfo();
        assertThat(pageInfo.totalPages()).isEqualTo(0);
        assertThat(pageInfo.totalElements()).isEqualTo(mockEmptyPage.getTotalElements());
    }

}
