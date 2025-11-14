package com.example.searchservice.selfpromotion.entity;

import java.time.Instant;
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

    @Field(type = FieldType.Text, analyzer = "self_promotion_index_analyzer", searchAnalyzer = "self_promotion_search_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "self_promotion_index_analyzer", searchAnalyzer = "self_promotion_search_analyzer")
    private String content;

    @Field(type = FieldType.Keyword)
    private String memberCode;

    @Field(type = FieldType.Keyword, name = "member_nickname")
    private String memberNickname;

    @Field(type = FieldType.Date, format = DateFormat.strict_date_optional_time_nanos, name = "updated_at")
    private Instant updatedAt;
}
