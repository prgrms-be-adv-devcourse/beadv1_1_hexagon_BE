package com.example.profileservice.resume.repository;

import com.example.profileservice.resume.model.entity.ResumeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {

    // memberCode로 단일 활성 이력서 조회
    Optional<ResumeEntity> findByMemberCodeAndIsDeletedFalse(String memberCode);

    // resumeCode로 이력서 상세 조회 (삭제되지 않은 것만)
    Optional<ResumeEntity> findByCodeAndIsDeletedFalse(String code);

    // resumeCode로 활성 상태의 이력서 존재 여부 확인
    boolean existsByCodeAndIsDeletedFalse(String code);
}
