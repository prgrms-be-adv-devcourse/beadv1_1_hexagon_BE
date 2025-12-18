-- 1. Members 테이블 데이터 생성
INSERT INTO members (code,
                     created_at,
                     updated_at,
                     is_deleted,
                     nick_name,
                     email,
                     phone_number,
                     birth_date,
                     gender,
                     provider,
                     provider_id,
                     role)
WITH RECURSIVE sequence AS (SELECT 1 AS n
                            UNION ALL
                            SELECT n + 1
                            FROM sequence
                            WHERE n < 30)
SELECT UUID(),                                                  -- code: 무조건 랜덤 UUID
       NOW(6),                                                  -- created_at
       NOW(6),                                                  -- updated_at
       0,                                                       -- is_deleted
       CONCAT('User_', n),                                      -- nick_name
       CONCAT('user', n, '@example.com'),                       -- email
       CONCAT('010-1234-', LPAD(n, 4, '0')),                    -- phone_number
       DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 10000) DAY), -- birth_date (랜덤)
       ELT(FLOOR(1 + RAND() * 2), 'MALE', 'FEMALE'),            -- gender (랜덤)
       ELT(FLOOR(1 + RAND() * 3), 'KAKAO', 'GOOGLE', 'NAVER'),  -- provider (랜덤)
       UUID(),                                                  -- provider_id
       CASE -- role (범위별 지정)
           WHEN n <= 5 THEN 'CLIENT' -- 1 ~ 5
           WHEN n <= 15 THEN 'FREELANCER' -- 6 ~ 15
           WHEN n <= 25 THEN 'BOTH' -- 16 ~ 25
           WHEN n <= 29 THEN 'NONE' -- 26 ~ 29
           ELSE 'ADMIN' -- 30
           END
FROM sequence;


-- 2. SocialMembers 테이블 데이터 생성 (Members와 이메일 연동)
INSERT INTO social_members (code,
                            created_at,
                            updated_at,
                            is_deleted,
                            email,
                            provider,
                            provider_id)
WITH RECURSIVE sequence AS (SELECT 1 AS n
                            UNION ALL
                            SELECT n + 1
                            FROM sequence
                            WHERE n < 30)
SELECT UUID(),                                                 -- code
       NOW(6),
       NOW(6),
       0,
       CONCAT('user', n, '@example.com'),                      -- email
       ELT(FLOOR(1 + RAND() * 3), 'KAKAO', 'GOOGLE', 'NAVER'), -- provider
       UUID()                                                  -- provider_id
FROM sequence;