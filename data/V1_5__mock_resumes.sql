/* 설명: Member ID 16~25번 사용자들에게 각각 5개씩, 총 50개의 이력서 데이터를 생성합니다.
  주의: member 테이블의 실제 이름이 'member' 혹은 'members'인지 확인이 필요합니다.
       아래 쿼리는 테이블명을 'member'로 가정했습니다.
*/

INSERT INTO resumes (code,
                     member_code,
                     title,
                     body,
                     link,
                     created_at,
                     updated_at,
                     is_deleted)
SELECT UUID(),                                                      -- resumes.code (유니크한 UUID 생성)
       m.code,                                                      -- resumes.member_code (member 테이블의 code 컬럼 복사)
       CONCAT('개발자 이력서 포트폴리오 - ', n.num),                           -- title (구분을 위해 숫자 붙임)
       CONCAT('안녕하세요. ', m.id, '번 회원의 ', n.num, '번째 자기소개서 내용입니다.'), -- body
       CONCAT('https://github.com/user/', m.code),                  -- link
       NOW(),                                                       -- created_at
       NOW(),                                                       -- updated_at
       FALSE                                                        -- is_deleted
FROM members m
         JOIN
     (
         -- 1명당 5개의 데이터를 만들기 위한 가상 테이블 (1~5)
         SELECT 1 AS num
         UNION ALL
         SELECT 2
         UNION ALL
         SELECT 3
         UNION ALL
         SELECT 4
         UNION ALL
         SELECT 5) n
WHERE m.id BETWEEN 16 AND 25;