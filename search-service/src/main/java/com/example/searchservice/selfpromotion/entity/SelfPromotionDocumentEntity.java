package com.example.searchservice.selfpromotion.entity;

import com.example.searchservice.common.vo.PaymentType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "self_promotions")
@Setting(settingPath = "/elasticsearch/self-promotions-settings.json")
public class SelfPromotionDocumentEntity {

    @Id
    private String code;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "self_promotion_index_analyzer", searchAnalyzer = "self_promotion_search_analyzer"),
            otherFields = {
                @InnerField(suffix = "completion", type = FieldType.Search_As_You_Type)
            }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "self_promotion_index_analyzer", searchAnalyzer = "self_promotion_search_analyzer")
    private String content;

    @Field(type = FieldType.Keyword)
    private String memberCode;

    @Field(type = FieldType.Keyword, name = "member_nickname")
    private String memberNickname;

    @Field(type = FieldType.Keyword, name = "payment_type")
    private PaymentType paymentType;

    @Field(type = FieldType.Long, name = "pay_amount")
    private Long payAmount;

    @Field(type = FieldType.Date, format = DateFormat.date_time, name = "updated_at")
    private Instant updatedAt;

    @Version
    Long version;
}
