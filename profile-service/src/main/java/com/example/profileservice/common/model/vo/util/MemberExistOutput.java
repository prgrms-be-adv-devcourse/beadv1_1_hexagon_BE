package com.example.profileservice.common.model.vo.util;

import java.util.List;

public record MemberExistOutput(
    List<String> exists,
    List<String> notExists
) {

    //혹시나 잘못 사용할 때를 대비해서 추가한 안전장치
    public MemberExistOutput(List<String> exists, List<String> notExists) {
        this.exists = exists == null ? List.of() : exists;
        this.notExists = notExists == null ? List.of() : notExists;
    }
}