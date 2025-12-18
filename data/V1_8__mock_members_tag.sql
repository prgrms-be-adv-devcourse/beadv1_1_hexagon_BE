INSERT IGNORE INTO members_tags (member_code, tag_code)
SELECT m.code, -- members 테이블의 code
       t.code  -- tags 테이블의 code
FROM (
         -- 회원 30명 조회
         SELECT code
         FROM members
         ORDER BY id
         LIMIT 30) m
         CROSS JOIN
     (
         -- 태그 4개 조회 (데이터가 충분하다면 LIMIT 숫자를 늘려도 됩니다)
         SELECT code
         FROM tags
         ORDER BY id
         LIMIT 4) t;