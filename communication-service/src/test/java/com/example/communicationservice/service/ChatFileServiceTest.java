package com.example.communicationservice.service;

import com.example.communicationservice.client.S3ServiceClient;
import com.example.communicationservice.client.dto.input.FileUploadUrlGenerateInput;
import com.example.communicationservice.client.dto.output.FileUploadUrlGenerateOutput;
import com.example.communicationservice.common.exception.ChatRoomException;
import com.example.communicationservice.common.status.ResponseDtoStatus;
import com.example.communicationservice.controller.dto.request.ChatFileUploadUrlGenerateRequest;
import com.example.communicationservice.controller.dto.response.ChatFileUploadUrlGenerateResponse;
import com.example.communicationservice.entity.ChatRoom;
import com.example.communicationservice.mapper.ChatMapper;
import com.example.communicationservice.repository.ChatRoomRepository;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.vo.ServiceName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatFileServiceTest {

    @InjectMocks
    private ChatFileService chatFileService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private S3ServiceClient s3ServiceClient;

    @Nested
    class ChatFileUploadUrlGenerateTest {
        private static final String ROOM_ID = "room-1";
        private static final String ROOM_NAME = "test-room";
        private static final String SENDER_CODE = "user-1";
        private static final String FILE_NAME = "test.png";
        private static final String CONTENT_TYPE = "image/png";

        // 파일 업로드 URL 생성 요청 dto
        private final ChatFileUploadUrlGenerateRequest request =
            new ChatFileUploadUrlGenerateRequest(FILE_NAME, CONTENT_TYPE);

        @Test
        void 정상적으로_파일_업로드_URL을_반환한다() {
            // given
            String receiverCode = "user-2";
            String fileKey = "generated/key";
            String queryString = "?query=string";
            List<String> memberCodes = List.of(SENDER_CODE, receiverCode);

            ChatRoom chatRoom = ChatRoom.builder()
                .name(ROOM_NAME)
                .memberCodes(memberCodes)
                .build();

            when(chatRoomRepository.findById(ROOM_ID)).thenReturn(Optional.of(chatRoom));

            FileUploadUrlGenerateOutput output =
                new FileUploadUrlGenerateOutput(fileKey, queryString);

            when(s3ServiceClient.generateUploadUrl(any(FileUploadUrlGenerateInput.class)))
                .thenReturn(ResponseDto.success(output));

            ChatFileUploadUrlGenerateResponse expectedResponse =
                new ChatFileUploadUrlGenerateResponse(
                    fileKey,
                    queryString
                );

            // when + then
            // try 안에서만 static mocking 유지
            try (MockedStatic<ChatMapper> mockedStatic = mockStatic(ChatMapper.class)) {
                mockedStatic.when(() -> ChatMapper.from(output))
                    .thenReturn(expectedResponse);

                // when
                ChatFileUploadUrlGenerateResponse actualResponse =
                    chatFileService.generateUploadUrl(ROOM_ID, SENDER_CODE, request);

                // then
                assertEquals(expectedResponse, actualResponse);

                ArgumentCaptor<FileUploadUrlGenerateInput> captor =
                    ArgumentCaptor.forClass(FileUploadUrlGenerateInput.class);

                verify(s3ServiceClient, times(1)).generateUploadUrl(captor.capture());
                assertEquals(ServiceName.CHATS, captor.getValue().serviceName());
            }
        }

        @Test
        void 채팅방_아이디에_해당되는_채팅방이_존재하지_않으면_CHATROOM_NOT_FOUND_예외가_발생한다() {
            // given
            when(chatRoomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

            // when & then
            ChatRoomException ex = assertThrows(
                ChatRoomException.class,
                () -> chatFileService.generateUploadUrl(ROOM_ID, SENDER_CODE, request)
            );

            assertEquals(ResponseDtoStatus.CHATROOM_NOT_FOUND, ex.getStatus());
        }

        @Test
        void URL_생성_요청자가_채팅방의_참여자가_아니면_CHATROOM_FORBIDDEN_예외가_발생한다() {
            // given
            List<String> memberCodes = List.of("other-user-1", "other-user-2"); // 요청자 코드 없음

            ChatRoom chatRoom = ChatRoom.builder()
                .name(ROOM_NAME)
                .memberCodes(memberCodes)
                .build();

            when(chatRoomRepository.findById(ROOM_ID)).thenReturn(Optional.of(chatRoom));

            // when & then
            ChatRoomException ex = assertThrows(
                ChatRoomException.class,
                () -> chatFileService.generateUploadUrl(ROOM_ID, SENDER_CODE, request)
            );

            assertEquals(ResponseDtoStatus.CHATROOM_FORBIDDEN, ex.getStatus());
        }
    }

}
