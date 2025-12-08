package org.hexagon.s3service.controller.swagger;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.dto.PresignedDownloadListResponse;
import org.hexagon.s3service.dto.PresignedDownloadRequestByCode;
import org.hexagon.s3service.dto.PresignedDownloadRequestByKey;
import org.hexagon.s3service.dto.PresignedUploadRequest;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.s3service.dto.StoreKeysRequest;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "S3 Internal API", description = "S3 내부 통신용 API")
public interface S3InternalControllerSwagger {

    public ResponseDto<PresignedUploadResponse> getUploadUrl(@Valid @RequestBody PresignedUploadRequest request);

    public ResponseDto<PresignedDownloadListResponse> getDownloadUrl(@RequestBody PresignedDownloadRequestByKey request);

    public ResponseDto<PresignedDownloadListResponse> getDownloadUrl(@RequestBody PresignedDownloadRequestByCode request);

    public ResponseDto<Empty> storeKeys(@RequestBody StoreKeysRequest request);

    public ResponseDto<Empty> updateKeys(@RequestBody StoreKeysRequest request);
}
