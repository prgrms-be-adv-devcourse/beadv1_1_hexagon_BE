package org.hexagon.s3service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hexagon.s3service.entity.vo.FileType;
import org.hexagon.core.vo.ServiceName;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "s3_resource")
public class S3Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column(name = "s3_key")
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type")
    private FileType fileType;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;
}
