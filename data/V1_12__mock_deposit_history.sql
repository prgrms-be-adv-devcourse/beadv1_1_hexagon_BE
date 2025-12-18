/*
 * V1_12__mock_deposit_history.sql (Fix: Can't reopen table error)
 * Deposit Histories 데이터 생성 (총 100건)
 * - 복잡한 서브쿼리 재참조를 제거하고 CROSS JOIN 방식으로 단순화
 */

SET FOREIGN_KEY_CHECKS = 0;

-- 1. [매핑 테이블] Deposit - Contract 1:1 매핑 (30쌍)
-- 서로 다른 코드 값과 섞이지 않도록 순번(ROW_NUMBER)으로 강제 매핑
CREATE TEMPORARY TABLE IF NOT EXISTS temp_deposit_mapping
(
    deposit_code  VARCHAR(36),
    contract_code VARCHAR(36)
);

INSERT INTO temp_deposit_mapping (deposit_code, contract_code)
SELECT d.code, c.code
FROM (SELECT code, ROW_NUMBER() OVER (ORDER BY code) as rn FROM deposits LIMIT 30) d
         JOIN
     (SELECT code, ROW_NUMBER() OVER (ORDER BY code) as rn FROM contracts LIMIT 30) c
     ON d.rn = c.rn;

-- 2. [기초 데이터 생성] 100개의 거래 내역 생성
-- 임시 테이블 재참조 문제를 피하기 위해 CROSS JOIN을 사용하여 한 번에 생성
CREATE TEMPORARY TABLE IF NOT EXISTS temp_raw_histories
(
    h_code        VARCHAR(36),
    deposit_code  VARCHAR(36),
    contract_code VARCHAR(36),
    change_amount BIGINT,
    summary       VARCHAR(255),
    created_at    DATETIME(6)
);

INSERT INTO temp_raw_histories (h_code, deposit_code, contract_code, change_amount, summary, created_at)
SELECT UUID(),
       m.deposit_code,
       m.contract_code,
       -- 금액: 입금(+) 또는 출금(-) 랜덤 (1만 ~ 50만)
       (CASE WHEN RAND() > 0.5 THEN 1 ELSE -1 END) * FLOOR(1 + RAND() * 50) * 10000,
       -- 적요: 랜덤 선택
       ELT(FLOOR(1 + RAND() * 5), '용역비 입금', '중도금 출금', '수수료', '보증금 환불', '예치금 충전'),
       -- 일시: 랜덤 시간
       DATE_ADD(NOW(), INTERVAL -FLOOR(RAND() * 100000) MINUTE)
FROM temp_deposit_mapping m
-- 30개의 매핑 데이터를 4배로 뻥튀기 (30 * 4 = 120 rows)
         CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) x
ORDER BY RAND()
LIMIT 100;
-- 그 중에서 100개만 랜덤 선택

-- 3. [잔액 계산 및 최종 삽입] Window Function 사용
INSERT INTO deposit_histories (code,
                               deposit_code,
                               contract_code,
                               change_amount,
                               summary,
                               result_amount,
                               created_at,
                               updated_at,
                               is_deleted)
SELECT h_code,
       deposit_code,
       contract_code,
       change_amount,
       summary,
       -- [핵심] 해당 계좌의 이전 내역을 모두 합산하여 현재 잔액 계산
       SUM(change_amount)
           OVER (PARTITION BY deposit_code ORDER BY created_at ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) as result_amount,
       created_at,
       created_at, -- updated_at 동일
       0
FROM temp_raw_histories;

-- 4. 임시 테이블 정리
DROP TEMPORARY TABLE IF EXISTS temp_deposit_mapping;
DROP TEMPORARY TABLE IF EXISTS temp_raw_histories;

SET FOREIGN_KEY_CHECKS = 1;