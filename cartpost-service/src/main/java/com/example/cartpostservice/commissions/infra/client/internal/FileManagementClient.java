package com.example.cartpostservice.commissions.infra.client.internal;

import com.example.cartpostservice.commissions.infra.client.internal.dto.request.DownloadFileComponentRequest;
import com.example.cartpostservice.commissions.infra.client.internal.dto.request.FilesRequestDto;
import com.example.cartpostservice.commissions.infra.client.internal.dto.response.DownloadFileComponentResponse;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "s3-service", path = "/internal/s3", contextId = "commissionFileClient")
public interface FileManagementClient {

    @PostMapping("/s3-resource")
    ResponseDto<Empty> registerFileStatus(@RequestBody FilesRequestDto filesRequestDto);


    @PatchMapping("/s3-resource")
    ResponseDto<Empty> updateFileStatus(@RequestBody FilesRequestDto filesRequestDto);


    @PostMapping("/download-url/code")
    ResponseDto<DownloadFileComponentResponse> getDownloadFileComponent(
            @RequestBody DownloadFileComponentRequest downloadFileComponentRequest);
}
