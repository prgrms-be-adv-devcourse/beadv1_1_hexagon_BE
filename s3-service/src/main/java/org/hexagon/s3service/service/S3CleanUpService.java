package org.hexagon.s3service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3CleanUpService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final Duration EXPIRATION = Duration.ofHours(24);
//    private static final Duration EXPIRATION = Duration.ZERO; // 테스트용

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

        String continuationToken = null;

        do {
            ListObjectsV2Request.Builder reqestBuilder = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(prefix);

            if (continuationToken != null) {
                reqestBuilder.continuationToken(continuationToken);
            }

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(reqestBuilder.build());

            // 1) EXPIRATION이 지난 객체만 수집
            List<ObjectIdentifier> deleteTargets = listResponse.contents().stream()
                    .filter(obj -> obj.lastModified().isBefore(threshold))
                    .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                    .toList();

            // 2) Bulk 삭제 (최대 1000개)
            if (!deleteTargets.isEmpty()) {
                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder().objects(deleteTargets).build())
                        .build();

                DeleteObjectsResponse deleteResponse = s3Client.deleteObjects(deleteRequest);

                deleteResponse.deleted().forEach(d ->
                        log.info("[S3 CLEANUP] Deleted temp file: {}", d.key())
                );
            }

            continuationToken = listResponse.nextContinuationToken();

        } while (continuationToken != null);
    }
}
