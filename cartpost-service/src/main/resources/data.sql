-- ==========================================
-- 1. 변수 설정
-- ==========================================

-- 1-1. 사용자 (작성자) 5명 정의
SET @user1 = RANDOM_UUID(); -- Alice (헤비 유저)
SET @user2 = RANDOM_UUID(); -- Bob (일반 유저)
SET @user3 = RANDOM_UUID(); -- Charlie (일반 유저)
SET @user4 = RANDOM_UUID(); -- David (휴면 유저)
SET @user5 = RANDOM_UUID(); -- Eve (신규 유저)

-- 1-2. 커미션(게시글) 10개의 고유 ID 정의
-- (commissions 테이블의 3번째 컬럼이자, tags 테이블의 연결 고리)
SET @c1 = RANDOM_UUID();
SET @c2 = RANDOM_UUID();
SET @c3 = RANDOM_UUID();
SET @c4 = RANDOM_UUID();
SET @c5 = RANDOM_UUID();
SET @c6 = RANDOM_UUID();
SET @c7 = RANDOM_UUID();
SET @c8 = RANDOM_UUID();
SET @c9 = RANDOM_UUID();
SET @c10 = RANDOM_UUID();


-- ==========================================
-- 2. commissions 테이블 데이터 생성 (10개)
-- 구조: (ID, 작성자_코드, 커미션_UUID, ...)
-- ==========================================
MERGE INTO commissions KEY("id") VALUES
-- [User 1: Alice] 3개의 커미션 작성 (활발한 활동)
    (1, @user1, @c1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 'SD 캐릭터 그려드립니다', '귀엽고 깜찍한 SD 스타일', 'PER_JOB', 30000, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP), TRUE, 'Alice'),
    (2, @user1, @c2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '방송용 배경음악 작곡', '유튜브 인트로/아웃트로 제작', 'PER_JOB', 150000, CURRENT_TIMESTAMP, DATEADD('DAY', 14, CURRENT_TIMESTAMP), TRUE, 'Alice'),
    (3, @user1, @c3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '장기 웹툰 어시스턴트 구함', '주 1회 채색 담당', 'MONTHLY', 800000, CURRENT_TIMESTAMP, DATEADD('MONTH', 6, CURRENT_TIMESTAMP), TRUE, 'Alice'),

-- [User 2: Bob] 2개의 커미션 작성
    (4, @user2, @c4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '일본어 번역 요청', 'A4 5장 분량 기술 문서', 'PER_JOB', 50000, CURRENT_TIMESTAMP, DATEADD('DAY', 3, CURRENT_TIMESTAMP), TRUE, 'Bob'),
    (5, @user2, @c5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '영상 자막 작업', '10분 내외 영상 컷편집 포함', 'PER_JOB', 70000, CURRENT_TIMESTAMP, DATEADD('DAY', 5, CURRENT_TIMESTAMP), TRUE, 'Bob'),

-- [User 3: Charlie] 1개의 커미션 작성 (마감 임박)
    (6, @user3, @c6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '급구) 로고 디자인', '내일 당장 필요합니다. 추가금 드려요.', 'PER_JOB', 200000, CURRENT_TIMESTAMP, DATEADD('HOUR', 24, CURRENT_TIMESTAMP), TRUE, 'Charlie'),

-- [User 4: David] 2개의 커미션 (하나는 삭제됨, 하나는 비활성)
    (7, @user4, @c7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE,  '삭제된 게시글입니다', '관리자에 의해 삭제됨', 'PER_JOB', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 'David'),
    (8, @user4, @c8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '잠시 작업 중단합니다', '개인 사정으로 쉽니다.', 'MONTHLY', 100000, CURRENT_TIMESTAMP, DATEADD('DAY', 30, CURRENT_TIMESTAMP), FALSE, 'David'),

-- [User 5: Eve] 2개의 커미션 (고액 예산)
    (9, @user5, @c9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '엔터프라이즈 서버 구축', 'AWS 기반 대규모 트래픽 처리', 'PER_JOB', 5000000, CURRENT_TIMESTAMP, DATEADD('MONTH', 2, CURRENT_TIMESTAMP), TRUE, 'Eve'),
    (10, @user5, @c10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, '모바일 앱 UI/UX 리뉴얼', '전체적인 디자인 개편', 'PER_JOB', 2500000, CURRENT_TIMESTAMP, DATEADD('MONTH', 1, CURRENT_TIMESTAMP), TRUE, 'Eve');


-- ==========================================
-- 3. commissions_tags 테이블 데이터 생성
-- 구조: (ID, 커미션_UUID, 태그_UUID)
-- ==========================================
MERGE INTO commissions_tags KEY("id") VALUES
-- Alice의 1번 커미션 (그림 관련) -> 태그 3개
    (1, @c1, RANDOM_UUID()), -- 태그: 그림
    (2, @c1, RANDOM_UUID()), -- 태그: SD
    (3, @c1, RANDOM_UUID()), -- 태그: 커미션

-- Alice의 2번 커미션 (음악 관련) -> 태그 2개
    (4, @c2, RANDOM_UUID()), -- 태그: 작곡
    (5, @c2, RANDOM_UUID()), -- 태그: BGM

-- Bob의 4번 커미션 (번역) -> 태그 1개
    (6, @c4, RANDOM_UUID()), -- 태그: 번역

-- Charlie의 6번 커미션 (급구, 디자인) -> 태그 2개
    (7, @c6, RANDOM_UUID()), -- 태그: 디자인
    (8, @c6, RANDOM_UUID()), -- 태그: 급구

-- David의 8번 커미션 (비활성 상태여도 태그는 존재) -> 태그 1개
    (9, @c8, RANDOM_UUID()),

-- Eve의 9번 커미션 (개발) -> 태그 3개
    (10, @c9, RANDOM_UUID()), -- 태그: 개발
    (11, @c9, RANDOM_UUID()), -- 태그: AWS
    (12, @c9, RANDOM_UUID()), -- 태그: 서버

-- Eve의 10번 커미션 (디자인) -> 태그 2개
    (13, @c10, RANDOM_UUID()), -- 태그: UIUX
    (14, @c10, RANDOM_UUID()); -- 태그: 앱디자인

-- carts 데이터 삽입
INSERT INTO carts (id, code, created_at, updated_at, is_deleted, member_code) VALUES
                                                                                  (1, 'cart-1111-uuid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 'member-uuid-001'),
                                                                                  (2, 'cart-2222-uuid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 'member-uuid-002'),
                                                                                  (3, 'cart-3333-uuid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 'member-uuid-003');

-- cart_items 데이터 삽입
INSERT INTO cart_items (id, code, contract_code, cart_code, status, started_at, ended_at, payment_type, amount) VALUES
                                                                                                                    (1, 'item-uuid-001', 'contract-uuid-001', 'cart-1111-uuid', 'CONFIRMED', CURRENT_TIMESTAMP, DATEADD('DAY', 30, CURRENT_TIMESTAMP), 'MONTHLY', 50000),
                                                                                                                    (2, 'item-uuid-002', 'contract-uuid-002', 'cart-2222-uuid', 'PAID', CURRENT_TIMESTAMP, DATEADD('DAY', 15, CURRENT_TIMESTAMP), 'ONE_TIME', 150000),
                                                                                                                    (3, 'item-uuid-003', 'contract-uuid-003', 'cart-3333-uuid', 'CONFIRMED', CURRENT_TIMESTAMP, DATEADD('DAY', 45, CURRENT_TIMESTAMP), 'MONTHLY', 75000);
