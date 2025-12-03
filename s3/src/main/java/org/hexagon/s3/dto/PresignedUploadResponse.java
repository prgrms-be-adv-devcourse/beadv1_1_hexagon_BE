package org.hexagon.s3.dto;

/**
 * 전체 Presigned Url = (버킷, region 정보) + key + parameters
 * ex) https://team01-hexagon-bucket.s3.ap-northeast-2.amazonaws.com/commissions/d55b33a3-923d-4cbe-bde9-99952d892f93-test.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251202T030357Z&X-Amz-SignedHeaders=content-type%3Bhost&X-Amz-Credential=AKIA37MLSL4WWTOIITOU%2F20251202%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Expires=300&X-Amz-Signature=5f5a0be7890ea97f9401a23b3c878365f855020f070749ec9bf39ce6dae2fae3
 */
public record PresignedUploadResponse(
        String key,
        String queryString
) {

}
