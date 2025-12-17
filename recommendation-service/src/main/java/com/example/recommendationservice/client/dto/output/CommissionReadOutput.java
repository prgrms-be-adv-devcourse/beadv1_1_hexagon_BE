package com.example.recommendationservice.client.dto.output;

import org.hexagon.core.vo.PaymentType;

public record CommissionReadOutput(
    String title,
    String content,
    PaymentType paymentType,
    Long unitAmount
) {
    // 쿼리를 위한 텍스트 변환
    public String toQueryText() {
        StringBuilder sb = new StringBuilder();

        String commissionTitle = nullToEmpty(title);
        String commissionContent = nullToEmpty(content);

        if (!commissionTitle.isBlank()) {
            sb.append("의뢰글 제목: ").append(commissionTitle).append("\n");
        }

        if (!commissionContent.isBlank()) {
            sb.append("의뢰글 내용: ").append(commissionContent).append("\n");
        }

        return sb.toString().trim();
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
