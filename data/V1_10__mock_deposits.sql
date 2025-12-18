/*
 * V8__seed_deposits.sql
 * Deposits 데이터 생성 (총 31건)
 * - 1. 특정 멤버 (32dd90c2...) 데이터 1건 (고정)
 * - 2. Members 테이블의 모든 회원(30명) 데이터 1건씩 생성 (순차 조회)
 */

SET FOREIGN_KEY_CHECKS = 0;

-- 1. [특정 멤버] 데이터 1건 생성 (100만원)
INSERT INTO deposits (code,
                      member_code,
                      amount,
                      created_at,
                      updated_at,
                      is_deleted)
VALUES (UUID(),
        '32dd90c2-9114-4425-9523-8d48fd348db0', -- 특정 멤버 코드
        1000000,
        NOW(6),
        NOW(6),
        0);

-- 2. [기존 멤버] Members 테이블의 모든 회원(30명) 데이터 생성
-- 랜덤이 아니라 테이블에 있는 순서대로 가져와서 매핑
INSERT INTO deposits (code,
                      member_code,
                      amount,
                      created_at,
                      updated_at,
                      is_deleted)
SELECT UUID(),
       code,
       FLOOR(1 + RAND() * 100) * 10000, -- 금액은 1만원~100만원 사이 랜덤
       NOW(6),
       NOW(6),
       0
FROM members;
-- members 테이블에 30명이 있다면 정확히 30개가 추가되어 총 31개가 됩니다.

SET FOREIGN_KEY_CHECKS = 1;