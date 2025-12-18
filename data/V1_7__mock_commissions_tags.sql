SET FOREIGN_KEY_CHECKS = 0;

-- 1. 중복 방지 및 데이터 취합을 위한 임시 테이블 생성
CREATE TEMPORARY TABLE IF NOT EXISTS temp_commissions_tags
(
    comm_code VARCHAR(36),
    tag_code  VARCHAR(36)
);

-- 2. [기본 매핑] 모든 Commission에 대해 최소 1개의 태그 부여 (60개)
INSERT INTO temp_commissions_tags (comm_code, tag_code)
SELECT c.code,
       (SELECT code FROM tags ORDER BY RAND() LIMIT 1) -- 랜덤 태그 1개
FROM commissions c;

-- 3. [추가 매핑 1] 약 60%의 Commission에 두 번째 태그 부여
INSERT INTO temp_commissions_tags (comm_code, tag_code)
SELECT c.code,
       (SELECT code FROM tags ORDER BY RAND() LIMIT 1)
FROM commissions c
WHERE RAND() < 0.6;

-- 4. [추가 매핑 2] 약 30%의 Commission에 세 번째 태그 부여
INSERT INTO temp_commissions_tags (comm_code, tag_code)
SELECT c.code,
       (SELECT code FROM tags ORDER BY RAND() LIMIT 1)
FROM commissions c
WHERE RAND() < 0.3;

-- 5. 실제 테이블에 데이터 삽입 (중복 제거)
-- 임시 테이블에서 (comm_code, tag_code) 조합이 중복되지 않도록 DISTINCT 사용
INSERT INTO commissions_tags (commission_code, tag_code)
SELECT DISTINCT comm_code, tag_code
FROM temp_commissions_tags
WHERE comm_code IS NOT NULL
  AND tag_code IS NOT NULL;

-- 6. 임시 테이블 삭제
DROP TEMPORARY TABLE IF EXISTS temp_commissions_tags;

SET FOREIGN_KEY_CHECKS = 1;