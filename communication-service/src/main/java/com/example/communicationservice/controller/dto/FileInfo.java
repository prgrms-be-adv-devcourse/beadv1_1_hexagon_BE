package com.example.communicationservice.controller.dto;

import com.example.communicationservice.entity.File;

public record FileInfo(
    String key
) {
    public static FileInfo from(File file) {
        if (file == null) {
            return null;
        }

        return new FileInfo(file.getKey());
    }

    public File toDomain() {
        return File.builder().key(key).build();
    }
}
