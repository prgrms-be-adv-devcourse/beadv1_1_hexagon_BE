/*
 * V7__seed_self_promotions.sql
 * Self Promotions 데이터 생성
 * - Members 테이블의 ID 6~15, 16~25 범위 회원 (총 20명) 대상
 * - 각 회원당 1개의 홍보글 생성
 * - 직군별(웹, 서버, 앱, 디자인) 한국어 콘텐츠 매핑
 */

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO self_promotions (code,
                             member_code,
                             title,
                             content,
                             payment_type,
                             unit_amount,
                             resume_code,
                             pdf_key,
                             created_at,
                             updated_at,
                             is_deleted)
SELECT UUID(),                           -- code
       m.code,                           -- member_code
       -- [제목 생성] 카테고리에 맞춰 랜덤 선택
       CASE t.cat_id
           WHEN 1 THEN ELT(FLOOR(1 + RAND() * 2), 'React/Next.js 기반의 모던 웹 프론트엔드 개발해 드립니다.', '사용자 경험을 중시하는 반응형 웹 사이트 제작')
           WHEN 2 THEN ELT(FLOOR(1 + RAND() * 2), 'Java/Spring Boot 백엔드 서버 구축 및 API 개발',
                           '대용량 트래픽 처리가 가능한 안정적인 서버 개발자입니다.')
           WHEN 3 THEN ELT(FLOOR(1 + RAND() * 2), 'Flutter를 이용한 iOS/Android 크로스 플랫폼 앱 개발', '네이티브 앱 유지보수 및 신규 기능 개발')
           ELSE ELT(FLOOR(1 + RAND() * 2), '트렌디하고 직관적인 UI/UX 디자인 작업해 드립니다.', '브랜드 가치를 높이는 로고 및 웹 디자인')
           END as title,
       -- [내용 생성] 카테고리에 맞춰 상세 내용 매핑
       CASE t.cat_id
           WHEN 1
               THEN '안녕하세요. 5년 차 프론트엔드 개발자입니다.\n웹 표준과 접근성을 준수하며, React와 TypeScript를 주력으로 사용합니다.\n재사용 가능한 컴포넌트 설계와 퍼포먼스 최적화에 강점이 있습니다.\n피그마 디자인을 픽셀 퍼펙트하게 구현해 드립니다.'
           WHEN 2
               THEN '확장성 있는 백엔드 시스템을 구축합니다.\nSpring Boot와 JPA를 활용한 RESTful API 설계 경험이 풍부합니다.\nAWS 인프라 구축 및 배포 자동화(CI/CD)까지 전반적인 백엔드 업무를 수행할 수 있습니다.\n기존 레거시 코드 리팩토링도 환영합니다.'
           WHEN 3
               THEN '아이디어만 가져오시면 앱으로 만들어 드립니다.\nFlutter를 사용하여 안드로이드와 iOS 앱을 동시에 개발합니다.\n상용 앱 출시 경험 다수 보유하고 있으며, 스토어 등록 심사 과정까지 꼼꼼하게 챙겨드립니다.\n유지보수가 용이한 클린 코드를 작성합니다.'
           ELSE '사용자 중심의 디자인을 추구하는 디자이너입니다.\n단순히 예쁜 디자인이 아닌, 사용자가 편리하게 사용할 수 있는 UI/UX를 설계합니다.\n모바일 앱, 웹 대시보드, 랜딩 페이지 등 다양한 프로젝트 경험이 있습니다.\n원활한 커뮤니케이션으로 원하시는 결과물을 만들어 드립니다.'
           END as content,
       -- [Payment Type & Amount]
       ELT(FLOOR(1 + RAND() * 2), 'PER_JOB', 'MONTHLY'),
       FLOOR(1 + RAND() * 10) * 1000000, -- 100만 ~ 1000만 원
       NULL,                             -- resume_code (없음 처리)
       NULL,                             -- pdf_key (없음 처리)
       NOW(6),
       NOW(6),
       0
FROM
    -- 1. 대상 멤버를 조회하고 랜덤 카테고리(1~4)를 부여하는 가상 테이블
    (SELECT code,
            FLOOR(1 + RAND() * 4) as cat_id -- 1:Web, 2:Server, 3:App, 4:Design
     FROM members
     WHERE (id BETWEEN 6 AND 15)
        OR (id BETWEEN 16 AND 25)) as t
        JOIN members m ON t.code = m.code;

SET FOREIGN_KEY_CHECKS = 1;