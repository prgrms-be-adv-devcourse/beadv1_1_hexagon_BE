package org.hexagon.s3service.repository;

import java.util.List;
import org.hexagon.s3service.entity.S3Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface S3ResourceRepository extends JpaRepository<S3Resource, Long> {

    @Query("select r.key from S3Resource r where r.code = :code order by r.uploadedAt asc")
    List<String> findKeysByCode(@Param("code") String code);
}
