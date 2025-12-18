/*
 * V4__seed_commissions.sql
 * Commissions 데이터 생성 (기존 Members 테이블 참조) - 상세 공고 버전
 * - 기존 Members 테이블의 ID 1~5(CLIENT), 16~25(BOTH) 범위의 회원 코드를 사용하여 50개 생성
 * - 특정 회원(32dd90c2...) 10개 추가 생성
 */

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 임시 테이블 생성 (데이터 준비용)
CREATE TEMPORARY TABLE IF NOT EXISTS temp_seed_commissions
(
    temp_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    gen_comm_code   VARCHAR(36),  -- 새로 생성될 커미션 코드
    target_mem_code VARCHAR(36),  -- members 테이블에서 가져온 회원 코드
    writer_name     VARCHAR(255), -- members 테이블에서 가져온 닉네임
    cat_type        INT,          -- 1:Frontend, 2:Backend, 3:DevOps, 4:Design
    pay_type        VARCHAR(20),
    amount          BIGINT,
    start_dt        DATE,
    end_dt          DATE,
    r_status        VARCHAR(20),
    apply_cap       INT,
    sel_cap         INT
);

-- 2. [랜덤 50개 생성] - Members 테이블에서 직접 조회하여 매핑
INSERT INTO temp_seed_commissions (gen_comm_code, target_mem_code, writer_name, cat_type, pay_type, amount, start_dt,
                                   end_dt, r_status, apply_cap, sel_cap)
WITH RECURSIVE seq AS (SELECT 1 AS n
                       UNION ALL
                       SELECT n + 1
                       FROM seq
                       WHERE n < 50)
SELECT UUID(),
       (SELECT code
        FROM members
        WHERE (id BETWEEN 1 AND 5) OR (id BETWEEN 16 AND 25)
        ORDER BY RAND()
        LIMIT 1),             -- 매 행마다 랜덤 조회
       NULL,                  -- 이름은 나중에 UPDATE로 채움
       FLOOR(1 + RAND() * 4), -- 1~4 카테고리
       ELT(FLOOR(1 + RAND() * 2), 'MONTHLY', 'PER_JOB'),
       FLOOR(1 + RAND() * 10) * 500000,
       DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 20) DAY),
       DATE_ADD(CURDATE(), INTERVAL FLOOR(30 + RAND() * 100) DAY),
       ELT(FLOOR(1 + RAND() * 3), 'OPEN', 'CLOSED', 'HALTED'),
       FLOOR(5 + RAND() * 45),
       FLOOR(1 + RAND() * 5)
FROM seq;

-- 3. [특정 유저 10개 추가] (32dd90c2...)
INSERT INTO temp_seed_commissions (gen_comm_code, target_mem_code, writer_name, cat_type, pay_type, amount, start_dt,
                                   end_dt, r_status, apply_cap, sel_cap)
WITH RECURSIVE seq_spec AS (SELECT 1 AS n
                            UNION ALL
                            SELECT n + 1
                            FROM seq_spec
                            WHERE n < 10)
SELECT UUID(),
       '32dd90c2-9114-4425-9523-8d48fd348db0', -- 고정 멤버 코드
       NULL,
       FLOOR(1 + RAND() * 4),
       'PER_JOB',
       10000000,
       CURDATE(),
       DATE_ADD(CURDATE(), INTERVAL 90 DAY),
       'OPEN',
       20,
       1
FROM seq_spec;

-- 4. 닉네임 일괄 업데이트 (JOIN 사용)
UPDATE temp_seed_commissions t
    JOIN members m ON t.target_mem_code = m.code
SET t.writer_name = m.nick_name;

UPDATE temp_seed_commissions
SET writer_name = 'Unknown'
WHERE writer_name IS NULL;


-- 5. [실제 테이블 INSERT] - Commissions (상세 공고 적용)
INSERT INTO commissions (code, member_code, writer_name, title, content,
                         payment_type, unit_amount, started_at, ended_at, recruitment_status,
                         cache_apply_capacity, cache_applied_count, cache_selection_capacity, cache_selected_count,
                         last_sync_time, created_at, updated_at, is_deleted)
SELECT gen_comm_code,
       target_mem_code,
       writer_name,
       -- [상세 제목 생성]
       CASE cat_type
           WHEN 1 THEN ELT(FLOOR(1 + RAND() * 3), '반응형 쇼핑몰 웹사이트 프론트엔드 개발', '금융 대시보드 UI 개발 및 데이터 시각화',
                           '글로벌 교육 플랫폼 Next.js 마이그레이션')
           WHEN 2 THEN ELT(FLOOR(1 + RAND() * 3), '대용량 트래픽 처리 커머스 API 서버 개발', '실시간 채팅 서비스 백엔드 구축 (Node.js)',
                           'Python Django 기반 콘텐츠 관리 시스템(CMS) 개발')
           WHEN 3 THEN ELT(FLOOR(1 + RAND() * 2), 'AWS 클라우드 인프라 구축 및 자동화', 'Kubernetes 기반 CI/CD 파이프라인 고도화')
           WHEN 4 THEN ELT(FLOOR(1 + RAND() * 2), '핀테크 모바일 앱 UI/UX 리뉴얼 디자인', 'B2B SaaS 플랫폼 웹 디자인 및 브랜딩')
           END,
       -- [상세 내용 생성]
       CASE cat_type
           WHEN 1 THEN ELT(FLOOR(1 + RAND() * 3), '안녕하세요, 현재 운영 중인 의류 쇼핑몰의 리뉴얼 프로젝트를 함께하실 프리랜서 개발자분을 모십니다.

[주요 업무]
- Figma 디자인을 바탕으로 React 기반 웹사이트 퍼블리싱 및 개발
- PC/Mobile 반응형 웹 구현
- REST API 연동 (로그인, 상품 목록, 장바구니, 결제 등)

[필수 기술]
- React.js, TypeScript
- Tailwind CSS (또는 Styled Components)
- Git 협업 경험

[우대 사항]
- 쇼핑몰 개발 경험이 있으신 분
- 퍼포먼스 최적화 경험이 있으신 분

[일정]
- 기간: 착수일로부터 약 2개월
- 근무 방식: 원격 근무 가능 (주 1회 온오프라인 미팅)

관심 있으신 분들의 많은 지원 바랍니다.', '금융 데이터를 시각화하여 보여주는 관리자 대시보드 프론트엔드 개발 건입니다.

[프로젝트 개요]
- 복잡한 금융 데이터를 차트와 그리드로 표현하는 대시보드 구축
- 실시간 데이터 연동 및 상태 관리 최적화

[담당 업무]
- D3.js 또는 Chart.js를 활용한 데이터 시각화 컴포넌트 개발
- 복잡한 필터링 및 검색 기능 구현
- 사용자 권한에 따른 메뉴 및 기능 제어

[자격 요건]
- React, Redux/Recoil 능숙자
- 대용량 데이터 처리 및 렌더링 최적화 경험
- TypeScript 필수

[일정]
- 예상 기간: 3개월
- 시작일: 즉시 투입 가능

많은 지원 부탁드립니다.', '기존 서비스를 Next.js 기반으로 고도화하는 프로젝트입니다.

[주요 업무]
- 기존 CSR 기반 서비스를 SSR/SSG 환경(Next.js)으로 전환
- 다국어(i18n) 지원 및 SEO 최적화 작업
- 성능 개선 및 웹 접근성 준수

[필수 스킬]
- Next.js (App Router 경험 우대)
- Styled-components / Emotion
- GraphQL / Apollo Client 사용 경험

[우대 사항]
- 글로벌 서비스 런칭 경험
- 디자인 시스템 구축 경험

[근무 조건]
- 100% 원격 근무
- 슬랙 및 노션 활용 원활한 커뮤니케이션 필수')
           WHEN 2 THEN ELT(FLOOR(1 + RAND() * 3), '대규모 트래픽이 발생하는 커머스 플랫폼의 백엔드 개발자를 찾습니다.

[주요 업무]
- 주문/결제 프로세스 API 설계 및 개발
- 대용량 트래픽 처리를 위한 DB 쿼리 튜닝 및 캐싱 전략 수립
- MSA 환경에서의 서비스 간 통신 구현 (Kafka, gRPC)

[자격 요건]
- Java (Spring Boot) 또는 Kotlin 숙련자
- MySQL, Redis 운영 및 개발 경험
- 대규모 시스템 설계 경험

[우대 사항]
- 이커머스 도메인 경험자
- AWS 클라우드 환경 능숙자

[일정]
- 기간: 4개월 이상 (장기 계약 가능)', '실시간 커뮤니케이션 기능을 포함한 신규 서비스의 백엔드 구축을 의뢰합니다.

[프로젝트 내용]
- Socket.io를 활용한 실시간 채팅 서버 개발
- 채팅 내역 저장 및 검색을 위한 MongoDB 설계
- 푸시 알림 서버 연동 (FCM)

[필수 기술]
- Node.js (NestJS 선호)
- TypeScript
- MongoDB / Redis

[우대 사항]
- 실시간 서비스 운영 경험
- 클린 아키텍처에 대한 이해

[참고 사항]
- 기획 및 디자인은 완료된 상태입니다.', '영상 콘텐츠 관리를 위한 백오피스 및 API 서버 개발 프로젝트입니다.

[담당 업무]
- Django DRF를 이용한 RESTful API 개발
- 영상 파일 업로드 및 인코딩 프로세스 연동 (AWS MediaConvert 등)
- 관리자 페이지(Admin) 커스터마이징

[자격 요건]
- Python, Django Framework 능숙자
- PostgreSQL 사용 경험
- Docker 컨테이너 환경 개발 경험

[우대 사항]
- 영상 스트리밍 관련 지식 보유자
- TDD 기반 개발 습관')
           WHEN 3 THEN ELT(FLOOR(1 + RAND() * 2), '신규 서비스 런칭을 위한 AWS 인프라 구축을 의뢰합니다.

[주요 업무]
- Terraform을 활용한 IaC 기반 인프라 프로비저닝 (VPC, EC2, RDS 등)
- 고가용성(HA) 및 오토스케일링 아키텍처 구성
- 보안 그룹 및 IAM 권한 설계

[필수 요건]
- AWS 운영 경험 3년 이상
- Terraform 숙련자
- 네트워크 및 보안 지식

[일정]
- 기간: 1개월 (집중 근무)

안전하고 확장 가능한 인프라를 구축해주실 분을 찾습니다.', '현재 운영 중인 서비스의 배포 파이프라인을 개선하고 자동화하는 작업입니다.

[프로젝트 목표]
- Jenkins/GitLab CI를 GitHub Actions로 마이그레이션
- EKS(Kubernetes) 배포 자동화 (ArgoCD 도입 검토)
- 무중단 배포 전략 수립 (Blue/Green)

[필수 기술]
- Docker, Kubernetes (EKS)
- GitHub Actions
- Helm Chart 작성 능력

[우대 사항]
- 모니터링 시스템(Prometheus, Grafana) 구축 경험

[근무 방식]
- 주 1회 미팅 외 원격 근무')
           WHEN 4 THEN ELT(FLOOR(1 + RAND() * 2), '사용자 경험 개선을 위한 핀테크 앱 전면 리뉴얼 프로젝트입니다.

[업무 범위]
- 기존 앱 분석 및 UX 문제점 도출
- Figma를 활용한 UI 디자인 및 프로토타이핑
- 개발 가이드(Hand-off) 문서 작성 및 디자인 시스템 정리

[자격 요건]
- 모바일 앱 디자인 경력 3년 이상
- Figma 능숙자 (Auto Layout, Component, Variables 활용 필수)
- 금융/핀테크 분야 디자인 경험

[우대 사항]
- 인터랙션 디자인(Protopie 등) 가능자

[일정]
- 착수: 계약 즉시
- 기간: 2.5개월', '기업용 솔루션의 웹 디자인 및 브랜드 아이덴티티 수립을 의뢰합니다.

[주요 업무]
- 서비스 로고 및 브랜드 가이드라인 제작
- 반응형 웹 대시보드 UI 디자인
- 마케팅 랜딩 페이지 디자인

[필수 스킬]
- 웹/모바일 UI 디자인 역량
- 그래픽 리소스(아이콘, 일러스트) 제작 능력
- Adobe CC, Figma

[우대 사항]
- B2B 서비스 디자인 경험
- 논리적인 디자인 프로세스를 갖추신 분

포트폴리오 제출 필수입니다.')
           END,
       pay_type,
       amount,
       start_dt,
       end_dt,
       r_status,
       apply_cap,
       0,
       sel_cap,
       0,
       NOW(6),
       NOW(6),
       NOW(6),
       0
FROM temp_seed_commissions;


-- 6. [실제 테이블 INSERT] - CommissionsCapacity (1:1 매핑)
INSERT INTO commissions_capacity (commission_code, apply_capacity, applied_count, selection_capacity, selected_count)
SELECT gen_comm_code,
       apply_cap,
       0,
       sel_cap,
       0
FROM temp_seed_commissions;

-- 7. 임시 테이블 정리
DROP TEMPORARY TABLE IF EXISTS temp_seed_commissions;

SET FOREIGN_KEY_CHECKS = 1;