/*
 * V11__seed_settlements.sql
 * Settlements 데이터 생성 (500건)
 * - Contracts 테이블 기반 데이터 생성
 * - receiver_code = contracts.freelancer_code
 * - contract_code = contracts.code
 * - original_amount = contracts.unit_amount
 * - status = 'BEFORE' (고정)
 * - progressing_at = 현재 시간 (고정)
 */

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO settlements (code,
                         receiver_code,
                         contract_code,
                         original_amount,
                         settled_amount,
                         status,
                         progressing_at,
                         created_at,
                         settled_at,
                         settlement_rate)
SELECT UUID(),
       c.freelancer_code,
       c.code,
       c.unit_amount,
       NULL,     -- settled_amount (아직 정산 전이므로 NULL)
       'BEFORE', -- status 고정
       NOW(6),   -- progressing_at
       NOW(6),   -- created_at
       NULL,     -- settled_at
       NULL      -- settlement_rate
FROM contracts c
         -- 500개를 만들기 위해 contracts 테이블을 충분히 뻥튀기(Self Join 등)하거나
         -- contracts가 50개뿐이라면 이를 반복해서 500개를 만듦
         CROSS JOIN (SELECT 1
                     UNION
                     SELECT 2
                     UNION
                     SELECT 3
                     UNION
                     SELECT 4
                     UNION
                     SELECT 5
                     UNION
                     SELECT 6
                     UNION
                     SELECT 7
                     UNION
                     SELECT 8
                     UNION
                     SELECT 9
                     UNION
                     SELECT 10) x
LIMIT 500;
-- 설명: contracts가 50개라고 가정할 때, CROSS JOIN 10을 하면 500개가 생성됩니다.

SET FOREIGN_KEY_CHECKS = 1;