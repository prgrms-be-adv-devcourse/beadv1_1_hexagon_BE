/*
 * V9__seed_contracts.sql
 * Contracts 데이터 생성 (50건)
 * - Client: Members ID 1~5, 16~25, 또는 특정 UUID
 * - Freelancer: Members ID 6~15, 16~25, 또는 특정 UUID
 * - Status: REQUESTED, PAID, CANCELLED 중 랜덤
 */

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO contracts (code,
                       client_code,
                       freelancer_code,
                       commission_code,
                       name,
                       body,
                       payment_type,
                       unit_amount,
                       status,
                       started_at,
                       ended_at,
                       created_at,
                       updated_at,
                       is_deleted)
WITH RECURSIVE seq AS (SELECT 1 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 50)
SELECT UUID(),                                                  -- code
       -- 1. Client Code Selection
       (SELECT code
        FROM members
        WHERE (id BETWEEN 1 AND 5)
           OR (id BETWEEN 16 AND 25)
           OR code = '32dd90c2-9114-4425-9523-8d48fd348db0'
        ORDER BY RAND()
        LIMIT 1),
       -- 2. Freelancer Code Selection
       (SELECT code
        FROM members
        WHERE (id BETWEEN 6 AND 15)
           OR (id BETWEEN 16 AND 25)
           OR code = '32dd90c2-9114-4425-9523-8d48fd348db0'
        ORDER BY RAND()
        LIMIT 1),
       -- 3. Commission Code Selection (commissions 테이블에서 랜덤 연결)
       (SELECT code FROM commissions ORDER BY RAND() LIMIT 1),

       -- 4. Name & Body (한국어)
       CONCAT('외주 용역 계약서 - 프로젝트 #', n),
       '본 계약은 클라이언트와 프리랜서 간의 소프트웨어 개발 및 디자인 용역 수행을 위한 제반 사항을 규정함을 목적으로 한다. 과업의 범위, 수행 기간, 대금 지급 방식 등은 상호 협의하에 진행하며, 세부 내용은 별첨 기술서에 따른다.',

       -- 5. Payment Type
       ELT(FLOOR(1 + RAND() * 2), 'MONTHLY', 'PER_JOB'),

       -- 6. Unit Amount (100만 ~ 1000만)
       FLOOR(1 + RAND() * 10) * 1000000,

       -- 7. Status (REQUESTED, PAID, CANCELLED)
       ELT(FLOOR(1 + RAND() * 3), 'REQUESTED', 'PAID', 'CANCELLED'),

       -- 8. Dates
       NOW(6),                                                  -- started_at
       DATE_ADD(NOW(6), INTERVAL FLOOR(30 + RAND() * 180) DAY), -- ended_at
       NOW(6),                                                  -- created_at
       NOW(6),                                                  -- updated_at
       0                                                        -- is_deleted
FROM seq;

SET FOREIGN_KEY_CHECKS = 1;