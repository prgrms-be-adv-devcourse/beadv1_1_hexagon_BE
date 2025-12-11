package com.example.communicationservice.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    String key;

    @Builder
    private File(String key) {
        this.key = key;
    }

}
