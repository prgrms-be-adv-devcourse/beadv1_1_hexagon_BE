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

-- 2. [기초 데이터 생성] 100개의 거래 내역 랜덤 생성 (잔액 계산 전)
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
WITH RECURSIVE seq AS (SELECT 1 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 100)
SELECT UUID(),
       m.deposit_code,
       m.contract_code,
       -- 금액: -100만원 ~ +100만원 사이 (0 제외)
       (FLOOR(1 + RAND() * 200) - 100) * 10000,
       -- 적요: 랜덤 선택
       ELT(FLOOR(1 + RAND() * 5), '계약금 입금', '중도금 지급', '잔금 처리', '수수료 차감', '예치금 충전'),
       -- 일시: 최근 3개월 내 랜덤
       DATE_ADD(NOW(), INTERVAL -FLOOR(RAND() * 90 * 24 * 60) MINUTE)
FROM seq
         JOIN temp_deposit_mapping m
              ON 1 = 1
-- 30개의 매핑 중 랜덤 하나 선택
WHERE m.deposit_code = (SELECT deposit_code
                        FROM temp_deposit_mapping
                        ORDER BY RAND()
                        LIMIT 1);
-- 주의: 위 방식(WHERE 서브쿼리)은 모든 row에 같은 값이 들어갈 수 있음.
-- 더 정확한 분포를 위해 아래 방식으로 재입력합니다.

TRUNCATE TABLE temp_raw_histories;

INSERT INTO temp_raw_histories (h_code, deposit_code, contract_code, change_amount, summary, created_at)
SELECT UUID(),
       m.deposit_code,
       m.contract_code,
       -- 금액: 입금(+) 또는 출금(-) 랜덤
       CASE
           WHEN RAND() > 0.5 THEN FLOOR(1 + RAND() * 100) * 10000 -- 입금
           ELSE -FLOOR(1 + RAND() * 50) * 10000 -- 출금
           END,
       ELT(FLOOR(1 + RAND() * 5), '프로젝트 착수금', '검수 완료금', '계약 해지 환불', '플랫폼 수수료', '추가 예치금'),
       DATE_ADD(NOW(), INTERVAL -FLOOR(RAND() * 90 * 24 * 60) MINUTE)
FROM (WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 100) SELECT n FROM seq) nums
         JOIN temp_deposit_mapping m
              ON m.deposit_code = (SELECT deposit_code FROM temp_deposit_mapping ORDER BY RAND() LIMIT 1);
-- (참고: MySQL 옵티마이저 특성상 이 JOIN 방식도 단일값으로 고정될 수 있어, 아래와 같이 로직 보완)

-- [데이터 재생성] 더 확실한 랜덤 분배를 위해 루프 없이 INSERT ... SELECT 활용
DELETE
FROM temp_raw_histories;

INSERT INTO temp_raw_histories (h_code, deposit_code, contract_code, change_amount, summary, created_at)
SELECT UUID(),
       t.deposit_code,
       t.contract_code,
       CASE
           WHEN RAND() > 0.4 THEN FLOOR(1 + RAND() * 200) * 10000
           ELSE -FLOOR(1 + RAND() * 100) * 10000
           END,
       ELT(FLOOR(1 + RAND() * 5), '표준 용역비 입금', '정산금 지급', '수수료', '이벤트 지급', '충전'),
       DATE_ADD(NOW(), INTERVAL -FLOOR(RAND() * 100000) MINUTE)
FROM (
         -- 100개의 랜덤 매핑 인덱스를 생성
         SELECT (SELECT deposit_code FROM temp_deposit_mapping ORDER BY RAND() LIMIT 1) as deposit_code,
                (SELECT contract_code
                 FROM temp_deposit_mapping
                 WHERE deposit_code = deposit_code)                                     as contract_code -- 매핑 유지
         FROM (WITH RECURSIVE seq AS (SELECT 1 AS n UNION ALL SELECT n + 1 FROM seq WHERE n < 100) SELECT n FROM seq) s
         -- 서브쿼리 안에서 매번 RAND()가 실행되도록 유도 (MySQL 버전에 따라 동작 상이할 수 있으나 일반적으론 동작)
         -- 만약 단일값 문제 발생 시, 아래 JOIN 방식으로 보완
     ) as random_selection
         JOIN temp_deposit_mapping t ON t.deposit_code = random_selection.deposit_code;
-- (여기서도 의도대로 100개가 골고루 안 섞일 수 있으므로 가장 확실한 방법인 CROSS JOIN + RAND Order + Limit 사용)

-- [최종 확실한 방법]
-- 매핑 테이블을 여러번 복제해서 100개 이상 만들고 거기서 100개를 자르는 방식
DELETE
FROM temp_raw_histories;

INSERT INTO temp_raw_histories (h_code, deposit_code, contract_code, change_amount, summary, created_at)
SELECT UUID(),
       m.deposit_code,
       m.contract_code,
       (CASE WHEN RAND() > 0.5 THEN 1 ELSE -1 END) * FLOOR(1 + RAND() * 50) * 10000,
       ELT(FLOOR(1 + RAND() * 5), '용역비 입금', '중도금 출금', '수수료', '보증금 환불', '예치금 충전'),
       DATE_ADD(NOW(), INTERVAL -FLOOR(RAND() * 100000) MINUTE)
FROM temp_deposit_mapping m
         CROSS JOIN (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) x -- 30 * 4 = 120개 데이터 확보
ORDER BY RAND()
LIMIT 100;


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
       -- (초기 잔액을 0원으로 가정하고 변동분 누적)
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