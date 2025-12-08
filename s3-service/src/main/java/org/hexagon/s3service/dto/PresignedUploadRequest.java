package org.hexagon.s3service.dto;

import jakarta.validation.constraints.Pattern;
import org.hexagon.core.vo.ServiceName;

public record PresignedUploadRequest(
        ServiceName serviceName,
        String fileName,

        @Pattern(
                regexp = "^(image\\/jpeg|image\\/png|image\\/webp|application\\/pdf)$",
                message = "Unsupported content type"
        )
        String contentType
) {

}
