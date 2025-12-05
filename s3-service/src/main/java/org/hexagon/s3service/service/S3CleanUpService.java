package org.hexagon.s3service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class S3CleanUpService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Duration EXPIRATION = Duration.ofHours(24);
//    private static final Duration EXPIRATION = Duration.ZERO;

    private static final List<String> TEMP_PREFIXES = List.of(
            "commissions/temp/",
            "members/temp/",
            "self_promotions/temp/"
    );

    @Scheduled(cron = "0 0 3 * * *") // 매일 새벽 3시
    public void cleanupTempObjects() {
        Instant threshold = Instant.now().minus(EXPIRATION);

        for (String prefix : TEMP_PREFIXES) {
            deleteOldTempObjects(prefix, threshold);
        }
    }

    private void deleteOldTempObjects(String prefix, Instant threshold) {
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

        for (S3Object obj : listRes.contents()) {
            if (obj.lastModified().isBefore(threshold)) {
                deleteObject(obj.key());
            }
        }
    }

    private void deleteObject(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
