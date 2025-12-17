-- V2_2__add_self_promotions_data.sql

-- 1. 프로시저가 이미 존재하면 삭제합니다. (MySQL 구문)
DROP PROCEDURE IF EXISTS InsertSelfPromotionsData;

-- 2. Flyway 호환성을 위해 DELIMITER를 사용하지 않고 프로시저를 생성합니다.
-- 내부 구문은 MySQL의 기본 구분자(;)를 사용하되,
-- Flyway가 이를 분리자로 인식하지 않도록 특별한 주석(delimiter //)을 사용하여 Flyway의 처리 방식을 우회합니다.
-- 하지만 가장 안전한 방법은 프로시저 생성을 다른 파일로 분리하고 CALL만 남기는 것입니다.
-- 여기서는 프로시저 본문 전체를 하나의 SQL 블록으로 처리하도록 MySQL/Flyway가 인식할 수 있는 형태로 수정합니다.

-- Flyway가 이 블록을 하나의 구문으로 인식하게 하기 위해, BEGIN과 END 사이에
-- MySQL 기본 세미콜론을 유지하되, 내부 구문 분리를 MySQL 서버에 맡기도록 시도합니다.

CREATE PROCEDURE InsertSelfPromotionsData()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE random_job VARCHAR(50);
    DECLARE random_payment VARCHAR(20);
    DECLARE random_amount BIGINT;
    DECLARE long_content TEXT;
    DECLARE is_deleted_flag BOOLEAN;
    DECLARE created_dt DATETIME(6);
    DECLARE updated_dt DATETIME(6);
    DECLARE base_time DATETIME(6);
    DECLARE j INT;

    SET @jobs = '프론트엔드 개발자,백엔드 개발자,DevOps 개발자,퍼블리셔,UI 디자이너';
    SET base_time = UTC_TIMESTAMP(6);

    WHILE i < 1000
        DO

            SET random_job = SUBSTRING_INDEX(SUBSTRING_INDEX(@jobs, ',', 1 + FLOOR(RAND() * 5)), ',', -1);
            SET random_payment = IF(RAND() > 0.5, 'MONTHLY', 'PER_JOB');
            SET random_amount = 2000000 + FLOOR(RAND() * (5000000 - 2000000));
            SET is_deleted_flag = IF(RAND() > 0.8, TRUE, FALSE);
            SET created_dt = DATE_SUB(base_time, INTERVAL FLOOR(RAND() * 365) DAY);
            SET updated_dt = LEAST(base_time, DATE_ADD(created_dt, INTERVAL FLOOR(RAND() * 24 * 3600) SECOND));

            -- 5. content (30줄 이상) 생성
            SET long_content = CONCAT(
                    '## ', random_job, ' 경력 집중 소개 (총 경력 ', 3 + FLOOR(RAND() * 7), '년)', '\n\n',
                    '---', '\n',
                    '프리랜서 번호: ', i + 1, '\n',
                    '---', '\n\n'
                               );

            -- 직업별 전문성 및 기술 상세 목록
            SET long_content = CONCAT(long_content,
                                      '### 주요 기술 및 전문 영역\n',
                                      CASE random_job
                                          WHEN '프론트엔드 개발자'
                                              THEN '- React/Next.js 기반의 고성능 SSR/CSR 애플리케이션 개발\n- TypeScript를 활용한 타입 안정성 확보 및 대규모 코드 관리\n- Redux-Saga/Recoil을 이용한 복잡한 상태 관리 패턴 구현\n- 웹 접근성(WCAG) 및 SEO 최적화 경험\n- Webpack, Vite를 이용한 빌드 최적화 및 성능 개선\n'
                                          WHEN '백엔드 개발자'
                                              THEN '- Spring Boot와 Spring Cloud 기반의 Microservice Architecture 설계 및 구축\n- 대용량 처리를 위한 메시지 큐(Kafka, RabbitMQ) 연동 및 트랜잭션 관리\n- RESTful API 설계 및 문서화 (Swagger/OpenAPI)\n- 데이터베이스 튜닝(MySQL Indexing, Query Optimization) 및 Redis 캐싱 적용\n- AWS Lambda, Step Functions 등 Serverless 아키텍처 활용 경험\n'
                                          WHEN 'DevOps 개발자'
                                              THEN '- CI/CD 파이프라인(Jenkins, GitLab CI) 구축 및 자동화\n- AWS/GCP 클라우드 인프라 설계 및 Terraform을 이용한 IaC 구현\n- Kubernetes 클러스터 운영, Helm을 이용한 배포 관리\n- 모니터링 시스템(Prometheus, Grafana) 구축 및 알림 설정\n- Docker 컨테이너 보안 및 최적화\n'
                                          WHEN '퍼블리셔'
                                              THEN '- HTML5, CSS3 기반의 웹 표준 및 크로스 브라우징 완벽 지원\n- SASS/LESS를 활용한 체계적인 스타일 관리 및 CSS 아키텍처 설계\n- 다양한 디바이스에 대응하는 반응형/적응형 웹 구현\n- 인터랙티브한 웹 애니메이션(GSAP, Lottie) 구현 경험\n- 디자이너와의 협업을 위한 Figma, Sketch 활용 및 디자인 시스템 구축 참여\n'
                                          ELSE '- 사용자 중심 디자인(UCD) 프로세스 수행 및 와이어프레임/프로토타이핑\n- Figma, Sketch를 이용한 디자인 시스템(Design System) 구축 및 관리\n- Usability Test 및 A/B Test 결과를 기반으로 한 디자인 개선\n- 서비스 컨셉에 맞는 브랜드 가이드라인 및 UI/UX 원칙 정립\n- 개발팀과의 원활한 소통을 위한 디자인 핸드오프(Zeplin, Abstract) 경험\n'
                                          END
                               );

            -- 나머지 줄 수를 채우기 위한 상세 경력 설명 반복
            SET long_content = CONCAT(long_content, '\n### 프로젝트 경력 및 성과 (반복)\n');

            SET j = 0;
            WHILE j < 5
                DO
                    SET long_content = CONCAT(long_content,
                                              '**[Project ', j + 1, '] 대규모 커머스 플랫폼 구축 및 최적화**\n',
                                              '- 담당 역할: 핵심 기능 개발 및 성능 개선\n',
                                              '- 기여 사항: 서비스 응답 속도 ', 10 + FLOOR(RAND() * 20), '% 개선.\n',
                                              '- 사용 기술: ',
                                              CASE
                                                  WHEN j % 2 = 0 THEN 'MSA, Docker, React'
                                                  ELSE 'Monolith, Java, Vue.js' END, '\n',
                                              '- 주요 성과: 동시 접속자 수 ', 5000 + FLOOR(RAND() * 10000), '명 대응 아키텍처 구축\n'
                                       );
                    SET j = j + 1;
                END WHILE;

            -- 마무리 문구
            SET long_content = CONCAT(long_content, '\n### 문의 및 연락\n',
                                      '프로젝트에 대한 심층적인 논의는 언제든 환영합니다. 이력서 및 포트폴리오를 요청하시면 즉시 전달드리겠습니다. ',
                                      '최고의 결과물을 위해 열정과 책임감으로 임하겠습니다.\n\n',
                                      '감사합니다. (ID: ', i + 1, ')\n'
                               );

            -- INSERT 실행
            INSERT INTO self_promotions (code, member_code, resume_code, title, writerName, content, payment_type,
                                         unit_amount, is_deleted, created_at, updated_at)
            VALUES (UUID(), UUID(), UUID(), CONCAT('[', random_job, ']', '최상위 역량 보유 전문가의 홍보글입니다. (No.', i + 1, ')'),
                    CONCAT('Freelancer-', i + 1), long_content, random_payment, random_amount, is_deleted_flag,
                    created_dt, updated_dt);

            SET i = i + 1;
        END WHILE;
END;

-- 3. 프로시저 실행 (별도의 구문으로 인식)
CALL InsertSelfPromotionsData();

-- 4. 프로시저 정리 (선택 사항)
DROP PROCEDURE IF EXISTS InsertSelfPromotionsData;