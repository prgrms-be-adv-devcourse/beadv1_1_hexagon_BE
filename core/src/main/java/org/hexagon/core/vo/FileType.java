package org.hexagon.core.vo;

public enum FileType {
    IMAGE,
    PDF;

    public static FileType fromKey(String key) {
        // 확장자 추출
        String ext = key.substring(key.lastIndexOf('.') + 1).toLowerCase();

        return switch (ext) {
            case "jpg", "jpeg", "png", "webp" -> FileType.IMAGE;
            case "pdf" -> FileType.PDF;
            default -> throw new IllegalArgumentException("Unsupported file extension: " + ext);
        };
    }
}
