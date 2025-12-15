package com.example.communicationservice.mapper;

import com.example.communicationservice.client.dto.output.FileDownloadUrlGenerateOutput;
import com.example.communicationservice.client.dto.output.FileUploadUrlGenerateOutput;
import com.example.communicationservice.controller.dto.response.ChatFileReadResponse;
import com.example.communicationservice.controller.dto.request.ChatFileSendRequest;
import com.example.communicationservice.controller.dto.response.ChatFileSendResponse;
import com.example.communicationservice.controller.dto.response.ChatFileUploadUrlGenerateResponse;
import com.example.communicationservice.entity.File;

// dto <-> entity 또는 dto <-> dto 변환 로직 전담
public abstract class FileMapper {

    private FileMapper() {} // 인스턴스화 방지

    public static ChatFileReadResponse toReadResponse(FileDownloadUrlGenerateOutput output) {
        return new ChatFileReadResponse(
            output.key(),
            output.queryString(),
            output.fileType()
        );
    }

    public static File toEntity(ChatFileSendRequest request) {
        if (request == null) {
            return null;
        }

        return File.builder()
            .key(request.key())
            .build();
    }

    public static ChatFileUploadUrlGenerateResponse from(FileUploadUrlGenerateOutput output) {
        return new ChatFileUploadUrlGenerateResponse(
            output.key(),
            output.queryString()
        );
    }

    public static ChatFileSendResponse toSendResponse(FileDownloadUrlGenerateOutput output) {
        return new ChatFileSendResponse(
            output.key(),
            output.queryString(),
            output.fileType()
        );
    }

}
