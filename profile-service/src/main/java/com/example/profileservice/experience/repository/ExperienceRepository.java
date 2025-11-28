package com.example.profileservice.experience.repository;

import com.example.profileservice.experience.model.entity.ExperienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienceRepository extends JpaRepository<ExperienceEntity, Long> {

    // 특정 이력서에 속한 모든 경력/경험 조회 (삭제되지 않은 것만, 시작일 최신순)
    List<ExperienceEntity> findAllByResumeCodeAndIsDeletedFalseOrderByStartedAtDesc(String resumeCode);

    // experienceCode와 resumeCode를 모두 사용하여 경력/경험 상세 조회 (권한 검증용)
    Optional<ExperienceEntity> findByCodeAndResumeCodeAndIsDeletedFalse(String experienceCode, String resumeCode);

    // 특정 이력서 코드 목록에 속하는 모든 활성 경력/경험 항목을 조회 (일괄 삭제에 사용)
    List<ExperienceEntity> findAllByResumeCodeInAndIsDeletedFalse(Collection<String> resumeCodes);
}
