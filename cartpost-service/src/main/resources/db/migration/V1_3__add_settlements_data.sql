-- 기존에 같은 이름의 프로시저가 있다면 삭제
DROP PROCEDURE IF EXISTS insert_dummy_settlements;

DELIMITER $$

CREATE PROCEDURE insert_dummy_settlements()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE v_receiver_code CHAR(36);

    -- 2. 모든 데이터의 receiver_code는 1개의 UUID로 통일
    SET v_receiver_code = UUID();

    -- 1. 데이터 3000개 생성 반복문
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
            VALUES (UUID(), -- 3. code: 랜덤 UUID
                    v_receiver_code, -- 2. receiver_code: 위에서 생성한 고정 UUID
                    UUID(), -- 3. contract_code: 랜덤 UUID
                    FLOOR(1000 + (RAND() * 100000)), -- 7. original_amount: 1000 이상 (1000 ~ 101000 사이 랜덤)
                    NULL, -- 5. settled_amount: NULL
                    NULL, -- 5. settlement_rate: NULL
                    'BEFORE', -- 4. status: 'BEFORE'
                       -- 6. progressing_at: 2025-12-10 15:29:00 이전 (최근 30일 내 랜덤 시간 생성)
                    DATE_SUB('2025-12-10 15:29:00', INTERVAL FLOOR(RAND() * 30 * 24 * 60 * 60) SECOND),
                    NULL, -- 5. settled_at: NULL
                    NOW() -- created_at: 현재 시간
                   );
            SET i = i + 1;
        END WHILE;
END$$

DELIMITER ;

-- 프로시저 실행
CALL insert_dummy_settlements();

-- 사용한 프로시저 삭제
DROP PROCEDURE insert_dummy_settlements;