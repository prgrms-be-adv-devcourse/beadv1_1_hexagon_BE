-- 1. CART_ITEMS 테이블
CREATE TABLE IF NOT EXISTS cart_items (
                                          id            BIGINT NOT NULL AUTO_INCREMENT,
                                          code          VARCHAR(255) NOT NULL,
                                          contract_code VARCHAR(255) NOT NULL,
                                          cart_code     VARCHAR(255) NOT NULL,
                                          amount        VARCHAR(255) NOT NULL,
                                          payment_type  VARCHAR(50)  NOT NULL,
                                          status        VARCHAR(50)  NOT NULL,
                                          started_at    DATETIME(6)  NOT NULL,
                                          ended_at      DATETIME(6)  NOT NULL,

                                          PRIMARY KEY (id),
                                          UNIQUE (code)
);

-- 2. CARTS 테이블
CREATE TABLE IF NOT EXISTS carts (
                                     id          BIGINT NOT NULL AUTO_INCREMENT,
                                     code        VARCHAR(255) NOT NULL,
                                     member_code VARCHAR(255) NOT NULL,
                                     is_deleted  BIT(1) NOT NULL,
                                     created_at  DATETIME(6) NOT NULL,
                                     updated_at  DATETIME(6) NOT NULL,

                                     PRIMARY KEY (id),
                                     UNIQUE (code)
) ;

-- 3. COMMISSIONS 테이블
CREATE TABLE IF NOT EXISTS commissions (
                                           id           BIGINT NOT NULL AUTO_INCREMENT,
                                           code         VARCHAR(255) NOT NULL,
                                           writer_name  VARCHAR(255) NOT NULL,
                                           member_code  VARCHAR(255) NOT NULL,
                                           title        VARCHAR(255) NOT NULL,
                                           content      VARCHAR(255) NOT NULL,
                                           unit_amount  VARCHAR(255) NOT NULL,
                                           payment_type VARCHAR(50)  NOT NULL,
                                           is_open      BIT(1) NOT NULL,
                                           is_deleted   BIT(1) NOT NULL,
                                           started_at   DATE NOT NULL,
                                           ended_at     DATE NOT NULL,
                                           created_at   DATETIME(6) NOT NULL,
                                           updated_at   DATETIME(6) NOT NULL,

                                           PRIMARY KEY (id),
                                           UNIQUE (code)
);

-- commissions_tags
CREATE TABLE IF NOT EXISTS commissions_tags (
                                                id              VARCHAR(255) NOT NULL,
                                                commission_code VARCHAR(255) NOT NULL,
                                                tag_code        VARCHAR(255) NOT NULL,

                                                PRIMARY KEY (id),
                                                UNIQUE (commission_code, tag_code)
);

-- contracts
CREATE TABLE IF NOT EXISTS contracts (
                                         id              BIGINT NOT NULL AUTO_INCREMENT,
                                         code            CHAR(36) NOT NULL,
                                         requestor_code  CHAR(36) NOT NULL,
                                         contractor_code CHAR(36) NOT NULL,
                                         freelancer_code CHAR(36) NOT NULL,
                                         name            VARCHAR(255) NOT NULL,
                                         body            TEXT NOT NULL,
                                         payment_type    VARCHAR(255) NOT NULL,
                                         unit_amount     BIGINT NOT NULL,
                                         status          VARCHAR(255) NOT NULL,
                                         started_at      DATETIME(6) NOT NULL,
                                         ended_at        DATETIME(6) NOT NULL,
                                         is_deleted      BOOLEAN NOT NULL DEFAULT FALSE,
                                         created_at      DATETIME(6) NOT NULL,
                                         updated_at      DATETIME(6) NOT NULL,

                                         PRIMARY KEY (id)
);

-- deposits
CREATE TABLE IF NOT EXISTS deposits (
                                        id          BIGINT NOT NULL AUTO_INCREMENT,
                                        code        CHAR(36) NOT NULL,
                                        member_code CHAR(36) NOT NULL,
                                        amount      BIGINT NOT NULL,

                                        PRIMARY KEY (id)
);

-- deposit_histories
CREATE TABLE IF NOT EXISTS deposit_histories (
                                                 id            BIGINT NOT NULL AUTO_INCREMENT,
                                                 code          CHAR(36) NOT NULL,
                                                 deposit_code  CHAR(36) NOT NULL,
                                                 change_amount BIGINT NOT NULL,
                                                 result_amount BIGINT NOT NULL,
                                                 summary       VARCHAR(255) NOT NULL,
                                                 is_deleted    BOOLEAN NOT NULL DEFAULT FALSE,
                                                 created_at    DATETIME(6) NOT NULL,
                                                 updated_at    DATETIME(6) NOT NULL,

                                                 PRIMARY KEY (id)
);

-- settlements
CREATE TABLE IF NOT EXISTS settlements (
                                           id              BIGINT NOT NULL AUTO_INCREMENT,
                                           code            CHAR(36) NOT NULL,
                                           receiver_code   CHAR(36) NOT NULL,
                                           contract_code   CHAR(36) NOT NULL,
                                           original_amount BIGINT NOT NULL,
                                           settled_amount  BIGINT,
                                           settlement_rate DECIMAL(5, 2),
                                           status          VARCHAR(255) NOT NULL,
                                           progressing_at  DATETIME(6) NOT NULL,
                                           settled_at      DATETIME(6),
                                           created_at      DATETIME(6) NOT NULL,

                                           PRIMARY KEY (id)
);

-- experiences
CREATE TABLE IF NOT EXISTS experiences (
                                           id           BIGINT NOT NULL AUTO_INCREMENT,
                                           code         VARCHAR(255) NOT NULL,
                                           resume_code  VARCHAR(36) NOT NULL,
                                           title        VARCHAR(255) NOT NULL,
                                           organization VARCHAR(255) NOT NULL,
                                           description  TEXT NOT NULL,
                                           started_at   DATETIME(6) NOT NULL,
                                           ended_at     DATETIME(6),
                                           is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
                                           created_at   DATETIME(6) NOT NULL,
                                           updated_at   DATETIME(6) NOT NULL,

                                           PRIMARY KEY (id),
                                           UNIQUE (code)
);

-- ratings
CREATE TABLE IF NOT EXISTS ratings (
                                       id                BIGINT NOT NULL AUTO_INCREMENT,
                                       receiver_code     VARCHAR(36) NOT NULL,
                                       satisfied_count   INT NOT NULL DEFAULT 0,
                                       unsatisfied_count INT NOT NULL DEFAULT 0,

                                       PRIMARY KEY (id)
);

-- resumes
CREATE TABLE IF NOT EXISTS resumes (
                                       id          BIGINT NOT NULL AUTO_INCREMENT,
                                       code        VARCHAR(255) NOT NULL,
                                       member_code VARCHAR(36) NOT NULL,
                                       title       VARCHAR(255),
                                       body        TEXT,
                                       link        VARCHAR(512),
                                       is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
                                       created_at  DATETIME(6) NOT NULL,
                                       updated_at  DATETIME(6) NOT NULL,

                                       PRIMARY KEY (id),
                                       UNIQUE (code)
);

-- self_promotions
CREATE TABLE IF NOT EXISTS self_promotions (
                                               id           BIGINT NOT NULL AUTO_INCREMENT,
                                               code         VARCHAR(255) NOT NULL,
                                               member_code  VARCHAR(36) NOT NULL,
                                               resume_code  VARCHAR(36) NOT NULL,
                                               title        VARCHAR(255) NOT NULL,
                                               content      TEXT NOT NULL,
                                               payment_type VARCHAR(20) NOT NULL,
                                               unit_amount  BIGINT NOT NULL,
                                               is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
                                               created_at   DATETIME(6) NOT NULL,
                                               updated_at   DATETIME(6) NOT NULL,

                                               PRIMARY KEY (id),
                                               UNIQUE (code)
);

-- members_tags
CREATE TABLE IF NOT EXISTS members_tags (
                                            id          BIGINT NOT NULL AUTO_INCREMENT,
                                            member_code VARCHAR(36) NOT NULL,
                                            tag_code    VARCHAR(36) NOT NULL,

                                            PRIMARY KEY (id),
                                            UNIQUE (member_code, tag_code)
);

-- tags
CREATE TABLE IF NOT EXISTS tags (
                                    id    BIGINT NOT NULL AUTO_INCREMENT,
                                    code  VARCHAR(36) NOT NULL,
                                    skill VARCHAR(100) NOT NULL,

                                    PRIMARY KEY (id),
                                    UNIQUE (code),
                                    UNIQUE (skill)
);

-- members
CREATE TABLE IF NOT EXISTS members (
                                       id           BIGINT NOT NULL AUTO_INCREMENT,
                                       code         VARCHAR(36) NOT NULL,
                                       name         VARCHAR(255) NOT NULL,
                                       email        VARCHAR(255) NOT NULL,
                                       phone_number VARCHAR(255) NOT NULL,
                                       birth_date   DATE NOT NULL,
                                       gender       VARCHAR(255) NOT NULL,
                                       provider     VARCHAR(255) NOT NULL,
                                       provider_id  VARCHAR(255) NOT NULL,
                                       can_work     BOOLEAN NOT NULL,
                                       is_deleted   BOOLEAN NOT NULL DEFAULT FALSE,
                                       created_at   DATETIME(6) NOT NULL,
                                       updated_at   DATETIME(6) NOT NULL,

                                       PRIMARY KEY (id),
                                       UNIQUE (code),
                                       UNIQUE (name)
);

-- social_members
CREATE TABLE IF NOT EXISTS social_members (
                                              id          BIGINT NOT NULL AUTO_INCREMENT,
                                              code        VARCHAR(36) NOT NULL,
                                              email       VARCHAR(255) NOT NULL,
                                              provider    VARCHAR(255) NOT NULL,
                                              provider_id VARCHAR(255) NOT NULL,
                                              is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
                                              created_at  DATETIME(6) NOT NULL,
                                              updated_at  DATETIME(6) NOT NULL,

                                              PRIMARY KEY (id),
                                              UNIQUE (code)
);

-- payments
CREATE TABLE IF NOT EXISTS payments (
                                        id             BIGINT NOT NULL AUTO_INCREMENT,
                                        code           VARCHAR(255) NOT NULL,
                                        order_pg_id    VARCHAR(255) NOT NULL,
                                        payment_key    VARCHAR(255) NOT NULL,
                                        amount         BIGINT NOT NULL,
                                        payment_status VARCHAR(255) NOT NULL,
                                        method         VARCHAR(255) NOT NULL,
                                        approve_at     DATETIME(6) NOT NULL,
                                        is_deleted     BOOLEAN NOT NULL DEFAULT FALSE,
                                        created_at     DATETIME(6) NOT NULL,
                                        updated_at     DATETIME(6),

                                        PRIMARY KEY (id),
                                        UNIQUE (code)
);

-- orders
CREATE TABLE IF NOT EXISTS orders (
                                      id          BIGINT NOT NULL AUTO_INCREMENT,
                                      code        VARCHAR(255) NOT NULL,
                                      member_code VARCHAR(255) NOT NULL,
                                      order_pg_id VARCHAR(255) NOT NULL,
                                      is_deleted  BOOLEAN NOT NULL DEFAULT FALSE,
                                      created_at  DATETIME(6) NOT NULL,
                                      updated_at  DATETIME(6) NOT NULL,

                                      PRIMARY KEY (id),
                                      UNIQUE (code)
);