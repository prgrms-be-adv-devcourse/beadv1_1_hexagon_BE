SET FOREIGN_KEY_CHECKS = 0;

-- 1. Resumes 테이블의 code를 기반으로 Experiences 데이터 생성
INSERT INTO experiences (code,
                         resume_code,
                         title,
                         organization,
                         description,
                         started_at,
                         ended_at,
                         created_at,
                         updated_at,
                         is_deleted)
SELECT UUID(), -- Experiences 자체의 고유 코드
       code,   -- Resumes 테이블의 code (FK)
       -- [직무/활동명 랜덤 생성]
       ELT(FLOOR(1 + RAND() * 4),
           '시니어 소프트웨어 엔지니어',
           '프론트엔드 개발자',
           '백엔드 개발자',
           'DevOps 엔지니어'
       ),
       -- [조직/회사명 랜덤 생성]
       ELT(FLOOR(1 + RAND() * 5),
           '(주)테크솔루션',
           '넥스트젠 스타트업',
           '글로벌 IT 서비스',
           '클라우드 시스템즈',
           '이노베이션 랩'
       ),
       -- [설명 랜덤 생성 (한국어)]
       ELT(FLOOR(1 + RAND() * 3),
           'Spring Boot 기반의 대규모 트래픽 처리 시스템을 설계 및 구축했습니다. 기존 모놀리식 아키텍처를 MSA로 전환하여 배포 속도를 50% 향상시켰으며, 데이터베이스 쿼리 튜닝을 통해 응답 시간을 30% 단축했습니다.',
           'React와 TypeScript를 활용하여 사용자 친화적인 웹 인터페이스를 개발했습니다. 디자이너 및 기획자와 협업하여 UI/UX를 개선하고, 컴포넌트 재사용성을 높여 개발 생산성을 20% 증대시켰습니다.',
           'AWS 클라우드 인프라를 Terraform으로 자동화하고 관리했습니다. Jenkins와 Docker를 이용한 CI/CD 파이프라인을 구축하여 무중단 배포 환경을 구현했으며, 시스템 보안 취약점을 점검하고 보완했습니다.'
       ),
       -- [시작일: 1~5년 전 랜덤]
       DATE_SUB(NOW(), INTERVAL FLOOR(365 + RAND() * 1460) DAY),
       -- [종료일: 시작일로부터 약 6개월 ~ 1년 후]
       DATE_SUB(NOW(), INTERVAL FLOOR(30 + RAND() * 300) DAY),
       NOW(6),
       NOW(6),
       0
FROM resumes;

SET FOREIGN_KEY_CHECKS = 1;