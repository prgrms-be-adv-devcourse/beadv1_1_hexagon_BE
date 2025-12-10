package com.example.communicationservice.client;

import com.example.communicationservice.client.dto.input.FileUploadUrlGenerateInput;
import com.example.communicationservice.client.dto.output.FileUploadUrlGenerateOutput;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// name: 호출 대상 서비스 이름, path: 기본 경로
@FeignClient(
    name = "s3-service",
    path = "/internal/s3"
)
public interface S3ServiceClient {

    @PostMapping("/upload-url")
    ResponseDto<FileUploadUrlGenerateOutput> generateUploadUrl(@RequestBody FileUploadUrlGenerateInput input);

}
