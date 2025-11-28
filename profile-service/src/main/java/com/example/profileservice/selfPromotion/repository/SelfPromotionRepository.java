package com.example.profileservice.selfPromotion.repository;

import com.example.profileservice.selfPromotion.model.entity.SelfPromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SelfPromotionRepository extends JpaRepository<SelfPromotionEntity, String> {

    // 모든 활성(isDeleted=false) 프로모션을 최신순으로 조회
    List<SelfPromotionEntity> findAllByIsDeletedFalseOrderByCreatedAtDesc();

    // 특정 회원의 활성 프로모션을 조회
    Optional<SelfPromotionEntity> findByMemberCodeAndIsDeletedFalseOrderByCreatedAtDesc(String memberCode);

    // 특정 프로모션 코드로 활성 프로모션 단건 조회
    Optional<SelfPromotionEntity> findByCodeAndIsDeletedFalse(String code);

    // 특정 회원의 모든 활성 SelfPromotion 조회 (판매자 등록 취소에 사용)
    List<SelfPromotionEntity> findAllByMemberCodeAndIsDeletedFalse(String memberCode);
}
