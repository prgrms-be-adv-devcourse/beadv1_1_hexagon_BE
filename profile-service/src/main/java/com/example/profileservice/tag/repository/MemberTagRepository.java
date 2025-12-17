package com.example.profileservice.tag.repository;

import com.example.profileservice.tag.model.entity.MemberTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTagRepository extends JpaRepository<MemberTagEntity, Long> {

    // 특정 회원의 모든 연결된 태그 조회
    List<MemberTagEntity> findAllByMemberCode(String memberCode);

    // 특정 회원과 특정 태그의 연결 존재 여부 확인
    boolean existsByMemberCodeAndTagCode(String memberCode, String tagCode);

    // 특정 회원과 특정 태그의 연결 엔티티 조회
    Optional<MemberTagEntity> findByMemberCodeAndTagCode(String memberCode, String tagCode);

    // 특정 회원의 특정 태그 목록을 일괄 삭제 (동기화 효율화)
    @Modifying
    @Query("DELETE FROM MemberTagEntity mt WHERE mt.memberCode = :memberCode AND mt.tagCode IN :tagCodes")
    void deleteAllByMemberCodeAndTagCodeIn(String memberCode, Collection<String> tagCodes);

    // 특정 회원의 모든 연결된 태그를 일괄 삭제 (성능 최적화를 위해 Modifying 쿼리 사용)
    @Modifying
    @Query("DELETE FROM MemberTagEntity mt WHERE mt.memberCode = :memberCode")
    void deleteAllByMemberCode(@Param("memberCode") String memberCode);
}
