/* ------------------------------------------------------------
 * 1. Cart Related Tables
 * ------------------------------------------------------------ */

-- carts
CREATE TABLE IF NOT EXISTS carts
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(36)  NOT NULL,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6),
    member_code VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- cart_items
CREATE TABLE IF NOT EXISTS cart_items
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    code            VARCHAR(36)  NOT NULL,
    contract_code   VARCHAR(255) NOT NULL,
    cart_code       VARCHAR(255) NOT NULL,
    commission_code VARCHAR(255) NOT NULL,
    client_code     VARCHAR(255) NOT NULL,
    freelancer_code VARCHAR(255) NOT NULL,
    started_at      DATETIME(6)  NOT NULL,
    ended_at        DATETIME(6)  NOT NULL,
    payment_type    VARCHAR(255) NOT NULL,
    amount          VARCHAR(255) NOT NULL,
    client_name     VARCHAR(255),
    freelancer_name VARCHAR(255),
    contract_title  VARCHAR(255),

    PRIMARY KEY (id),
    UNIQUE (code)
);

/* ------------------------------------------------------------
 * 2. Commission & Promotion Tables
 * ------------------------------------------------------------ */

-- commissions
CREATE TABLE IF NOT EXISTS commissions
(
    id                       BIGINT       NOT NULL AUTO_INCREMENT,
    code                     VARCHAR(36)  NOT NULL,
    is_deleted               BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at               DATETIME(6)  NOT NULL,
    updated_at               DATETIME(6),
    member_code              VARCHAR(255) NOT NULL,
    title                    TEXT         NOT NULL,
    content                  TEXT         NOT NULL,
    payment_type             VARCHAR(255) NOT NULL,
    unit_amount              BIGINT       NOT NULL,
    started_at               DATE         NOT NULL,
    ended_at                 DATE         NOT NULL,
    recruitment_status       VARCHAR(255) NOT NULL,
    writer_name              VARCHAR(255) NOT NULL,
    cache_apply_capacity     INT,
    cache_applied_count      INT,
    cache_selection_capacity INT,
    cache_selected_count     INT,
    last_sync_time           DATETIME(6),

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- commissions_tags
CREATE TABLE IF NOT EXISTS commissions_tags
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    commission_code VARCHAR(255) NOT NULL,
    tag_code        VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

-- self_promotions
CREATE TABLE IF NOT EXISTS self_promotions
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    code         VARCHAR(36)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    member_code  VARCHAR(36)  NOT NULL,
    title        VARCHAR(255) NOT NULL,
    content      TEXT         NOT NULL,
    payment_type VARCHAR(20)  NOT NULL,
    unit_amount  BIGINT       NOT NULL,
    resume_code  VARCHAR(36),
    pdf_key      VARCHAR(255),

    PRIMARY KEY (id),
    UNIQUE (code)
);

/* ------------------------------------------------------------
 * 3. Contract & Settlement Tables
 * ------------------------------------------------------------ */

-- contracts
CREATE TABLE IF NOT EXISTS contracts
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    created_at      DATETIME(6)  NOT NULL,
    updated_at      DATETIME(6)  NOT NULL,
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    client_code     VARCHAR(36)  NOT NULL,
    freelancer_code VARCHAR(36)  NOT NULL,
    code            VARCHAR(36)  NOT NULL,
    commission_code VARCHAR(36)  NOT NULL,
    started_at      DATETIME(6)  NOT NULL,
    ended_at        DATETIME(6)  NOT NULL,
    payment_type    VARCHAR(255) NOT NULL,
    unit_amount     BIGINT       NOT NULL,
    status          VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    body            TEXT         NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- deposits
CREATE TABLE IF NOT EXISTS deposits
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE,
    member_code VARCHAR(36) NOT NULL,
    code        VARCHAR(36) NOT NULL,
    amount      BIGINT      NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- deposit_histories
CREATE TABLE IF NOT EXISTS deposit_histories
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    is_deleted    BOOLEAN      NOT NULL DEFAULT FALSE,
    deposit_code  VARCHAR(36)  NOT NULL,
    contract_code VARCHAR(36),
    code          VARCHAR(36)  NOT NULL,
    change_amount BIGINT       NOT NULL,
    summary       VARCHAR(255) NOT NULL,
    result_amount BIGINT       NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- settlements
CREATE TABLE IF NOT EXISTS settlements
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    code            VARCHAR(36)  NOT NULL,
    receiver_code   VARCHAR(36)  NOT NULL,
    contract_code   VARCHAR(36)  NOT NULL,
    original_amount BIGINT       NOT NULL,
    settled_amount  BIGINT,
    status          VARCHAR(255) NOT NULL,
    progressing_at  DATETIME(6)  NOT NULL,
    created_at      DATETIME(6)  NOT NULL,
    settled_at      DATETIME(6),
    settlement_rate DECIMAL(5, 2),

    PRIMARY KEY (id),
    UNIQUE (code)
);

/* ------------------------------------------------------------
 * 4. Payment & Order Tables
 * ------------------------------------------------------------ */

-- orders
CREATE TABLE IF NOT EXISTS orders
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(36)  NOT NULL,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6),
    member_code VARCHAR(255) NOT NULL,
    order_pg_id VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code),
    UNIQUE (order_pg_id)
);

-- payments
CREATE TABLE IF NOT EXISTS payments
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    code           VARCHAR(36)  NOT NULL,
    is_deleted     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6),
    order_pg_id    VARCHAR(255) NOT NULL,
    payment_key    VARCHAR(255),
    amount         BIGINT       NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    method         VARCHAR(255) NOT NULL,
    approve_at     DATETIME(6),

    PRIMARY KEY (id),
    UNIQUE (code),
    UNIQUE (order_pg_id)
);

/* ------------------------------------------------------------
 * 5. Resume, Experience & Rating Tables
 * ------------------------------------------------------------ */

-- resumes
CREATE TABLE IF NOT EXISTS resumes
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    code        VARCHAR(36) NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE,
    member_code VARCHAR(36) NOT NULL,
    title       VARCHAR(255),
    body        TEXT,
    link        VARCHAR(512),

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- experiences
CREATE TABLE IF NOT EXISTS experiences
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    code         VARCHAR(36)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    resume_code  VARCHAR(36)  NOT NULL,
    title        VARCHAR(255) NOT NULL,
    organization VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    started_at   DATETIME(6)  NOT NULL,
    ended_at     DATETIME(6),

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- ratings
CREATE TABLE IF NOT EXISTS ratings
(
    id                BIGINT      NOT NULL AUTO_INCREMENT,
    receiver_code     VARCHAR(36) NOT NULL,
    satisfied_count   INT         NOT NULL DEFAULT 0,
    unsatisfied_count INT         NOT NULL DEFAULT 0,

    PRIMARY KEY (id),
    UNIQUE (receiver_code)
);

/* ------------------------------------------------------------
 * 6. Common & Tag Tables
 * ------------------------------------------------------------ */

-- tags
CREATE TABLE IF NOT EXISTS tags
(
    id    BIGINT       NOT NULL AUTO_INCREMENT,
    skill VARCHAR(100) NOT NULL,
    code  VARCHAR(36)  NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (skill),
    UNIQUE (code)
);

-- members_tags
CREATE TABLE IF NOT EXISTS members_tags
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    member_code VARCHAR(36) NOT NULL,
    tag_code    VARCHAR(36) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (member_code, tag_code)
);


/* ------------------------------------------------------------
 * 7. Resource & Member Tables (Updated)
 * ------------------------------------------------------------ */

-- s3_resource
CREATE TABLE IF NOT EXISTS s3_resource
(
    id           BIGINT NOT NULL AUTO_INCREMENT,
    uploaded_at  DATETIME(6),
    s3_key       VARCHAR(255),
    service_code VARCHAR(255),
    file_type    VARCHAR(50),

    PRIMARY KEY (id)
);

-- members
CREATE TABLE IF NOT EXISTS members
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    code         VARCHAR(36)  NOT NULL,
    created_at   DATETIME(6)  NOT NULL,
    updated_at   DATETIME(6)  NOT NULL,
    is_deleted   BOOLEAN      NOT NULL DEFAULT FALSE,
    nick_name    VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    birth_date   DATE         NOT NULL,
    gender       VARCHAR(20)  NOT NULL,
    provider     VARCHAR(20)  NOT NULL,
    provider_id  VARCHAR(255) NOT NULL,
    role         VARCHAR(20)  NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);

-- social_members
CREATE TABLE IF NOT EXISTS social_members
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    code        VARCHAR(36)  NOT NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,
    email       VARCHAR(255) NOT NULL,
    provider    VARCHAR(20)  NOT NULL,
    provider_id VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (code)
);