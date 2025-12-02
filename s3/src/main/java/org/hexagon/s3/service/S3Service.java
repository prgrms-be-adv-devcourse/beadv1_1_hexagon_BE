package org.hexagon.s3.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hexagon.s3.dto.PresignedUploadResponse;
import org.hexagon.s3.dto.ServiceName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 전체 Presigned Url = (버킷, region 정보) + key + querystring
     * ex) https://team01-hexagon-bucket.s3.ap-northeast-2.amazonaws.com/commissions/d55b33a3-923d-4cbe-bde9-99952d892f93-test.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251202T030357Z&X-Amz-SignedHeaders=content-type%3Bhost&X-Amz-Credential=AKIA37MLSL4WWTOIITOU%2F20251202%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=300&X-Amz-Signature=5f5a0be7890ea97f9401a23b3c878365f855020f070749ec9bf39ce6dae2fae3
     */
    public PresignedUploadResponse createUploadUrl(ServiceName serviceName, String filename, String contentType) {
        String service = serviceName.toLower();
        String key = service + "/" + UUID.randomUUID().toString() + "-" + filename;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String url = presignedRequest.url().toString();

        // key + querystring만 추출
        String keyWithQuery = url.substring(url.indexOf(key));

        String query = keyWithQuery.substring(keyWithQuery.indexOf('?'));

        System.out.println("key = " + key);
        System.out.println("query = " + query);

        return new PresignedUploadResponse(
                key,
                query
        );
    }

}
