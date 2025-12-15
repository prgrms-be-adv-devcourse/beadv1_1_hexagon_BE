package org.hexagon.s3service.controller.swagger;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.dto.PresignedUploadRequest;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "S3 External API", description = "S3 외부 접근용 API")
public interface S3ControllerSwagger {
    
    public ResponseDto<PresignedUploadResponse> getUploadUrl(@Valid @RequestBody PresignedUploadRequest request);

    public ResponseDto<Empty> deleteObject(@RequestParam String key);
}
