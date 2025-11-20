CREATE TABLE tags (
    -- PK: 내부용 고유 ID
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

    -- skill: 태그의 실제 이름
    skill VARCHAR(100) NOT NULL UNIQUE,

    -- code: 외부 노출용 식별 코드 (UUID)
    code VARCHAR(36) NOT NULL UNIQUE,

    -- 인덱스
    INDEX idx_skill (skill)
);

CREATE TABLE members_tags (
    -- PK: 내부용 고유 ID
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,

    -- 연관된 멤버의 코드 FK
    member_code VARCHAR(36) NOT NULL,

    -- 연관된 태그의 코드 FK
    tag_code VARCHAR(36) NOT NULL,

    -- tag_code가 tags.code를 참조하도록 외래 키 설정
    CONSTRAINT FK_tag_code FOREIGN KEY (tag_code) REFERENCES tags(code),

    -- 복합 유니크 제약 조건
    CONSTRAINT UK_member_tag_unique UNIQUE (member_code, tag_code),

    -- 인덱스
    INDEX IDX_member_code (member_code),
    INDEX IDX_tag_code (tag_code)
);

CREATE TABLE self_promotions (
    -- BaseEntity 상속 필드
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(36) NOT NULL UNIQUE, -- 외부 식별자 (UUID)
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

    -- SelfPromotionEntity 필드
    member_code VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    payment_type VARCHAR(20) NOT NULL, -- Enum: MONTHLY, PER_JOB
    unit_amount BIGINT NOT NULL,
    resume_code VARCHAR(36), -- NULLable

    INDEX idx_member_code (member_code),
    INDEX idx_resume_code (resume_code) -- 이력서 코드로 검색할 때를 대비해 추가
);

-- Tag 초기 데이터 삽입
INSERT INTO tags (tag_code, skill) VALUES
('tag-java-uuid', 'Java'),
('tag-js-uuid', 'JavaScript'),
('tag-ts-uuid', 'TypeScript'),
('tag-spring-uuid', 'Spring'),
('tag-sb-uuid', 'Spring Boot'),
('tag-react-uuid', 'React'),
('tag-nextjs-uuid', 'Next.js'),
('tag-nodejs-uuid', 'Node.js'),
('tag-express-uuid', 'Express'),
('tag-nestjs-uuid', 'NestJS'),
('tag-kotlin-uuid', 'Kotlin'),
('tag-html-uuid', 'HTML'),
('tag-css-uuid', 'CSS'),
('tag-tailwind-uuid', 'Tailwind CSS'),
('tag-mysql-uuid', 'MySQL'),
('tag-postgresql-uuid', 'PostgreSQL'),
('tag-mongodb-uuid', 'MongoDB'),
('tag-redis-uuid', 'Redis'),
('tag-es-uuid', 'Elasticsearch'),
('tag-kafka-uuid', 'Kafka'),
('tag-aws-uuid', 'AWS'),
('tag-docker-uuid', 'Docker'),
('tag-k8s-uuid', 'Kubernetes'),
('tag-git-uuid', 'Git'),
('tag-github-uuid', 'GitHub'),
('tag-web-uuid', 'Web'),
('tag-backend-uuid', 'Backend'),
('tag-frontend-uuid', 'Frontend'),
('tag-fullstack-uuid', 'Fullstack'),
('tag-devops-uuid', 'DevOps'),
('tag-ai-uuid', 'AI');