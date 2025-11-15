package com.example.searchservice.commission.entity;

import com.example.searchservice.commission.common.PaymentType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "commissions", createIndex = false)
public class CommissionDocumentEntity {

    @Id
    private String code;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword, name = "member_nickname")
    private String memberNickname;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "started_at")
    private LocalDate startedAt;

    @Field(type = FieldType.Date, format = DateFormat.date, name = "ended_at")
    private LocalDate endedAt;

    @Field(type = FieldType.Keyword, name = "payment_type")
    private PaymentType paymentType;

    @Field(type = FieldType.Long, name = "pay_amount")
    private Long payAmount;

    @Field(type = FieldType.Boolean, name = "is_closed")
    private Boolean isClosed;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "updated_at")
    private Instant updatedAt;

}
