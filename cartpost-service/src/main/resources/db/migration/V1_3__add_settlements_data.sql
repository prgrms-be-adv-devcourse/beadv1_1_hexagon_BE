-- 1. 기존 프로시저가 있다면 삭제 (마이그레이션이 실패했을 때를 대비)
DROP PROCEDURE IF EXISTS insert_dummy_settlements;


-- 2. 저장 프로시저 생성
-- 이 구문은 Flyway가 BEGIN...END 블록 내부의 세미콜론을 무시하고
-- 전체를 하나의 CREATE PROCEDURE 구문으로 인식하게 하는 일반적인 Flyway/MySQL 패턴입니다.
CREATE PROCEDURE insert_dummy_settlements()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE v_receiver_code CHAR(36);

    SET v_receiver_code = UUID();

    WHILE i < 3000
        DO
            INSERT INTO settlements (code,
                                     receiver_code,
                                     contract_code,
                                     original_amount,
                                     settled_amount,
                                     settlement_rate,
                                     status,
                                     progressing_at,
                                     settled_at,
                                     created_at)
            VALUES (UUID(),
                    v_receiver_code,
                    UUID(),
                    FLOOR(1000 + (RAND() * 100000)),
                    NULL,
                    NULL,
                    'BEFORE',
                    DATE_SUB(UTC_TIMESTAMP(), INTERVAL FLOOR(RAND() * 30 * 24 * 60 * 60) SECOND), -- UTC_TIMESTAMP로 변경
                    NULL,
                    UTC_TIMESTAMP() -- created_at: UTC_TIMESTAMP로 변경
                   );
            SET i = i + 1;
        END WHILE;
END;

-- 3. 프로시저 실행 (데이터 삽입)
CALL insert_dummy_settlements();

-- 4. 사용한 프로시저 삭제 (마이그레이션 후 정리)
DROP PROCEDURE insert_dummy_settlements;