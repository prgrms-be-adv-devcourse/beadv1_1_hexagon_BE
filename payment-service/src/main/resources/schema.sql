-- cart-service

CREATE TABLE cart_items (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            code VARCHAR(255) NOT NULL UNIQUE,
                            contract_code VARCHAR(255) NOT NULL,
                            cart_code VARCHAR(255) NOT NULL,
                            status VARCHAR(255) NOT NULL,
                            started_at TIMESTAMP NOT NULL,
                            ended_at TIMESTAMP NOT NULL,
                            payment_type VARCHAR(255) NOT NULL,
                            amount VARCHAR(255) NOT NULL
);

CREATE TABLE carts (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       code VARCHAR(255) NOT NULL UNIQUE,
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP,
                       member_code VARCHAR(255) NOT NULL
);

CREATE TABLE commissions (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             code VARCHAR(255) NOT NULL UNIQUE,
                             is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP,
                             member_code VARCHAR(255),
                             title VARCHAR(255),
                             content TEXT,
                             payment_type VARCHAR(255),
                             unit_amount VARCHAR(255),
                             started_at DATE,
                             ended_at DATE,
                             is_open BOOLEAN,
                             writer_name VARCHAR(255)
);

CREATE TABLE commissions_tags (
                                  id VARCHAR(255) PRIMARY KEY,
                                  commission_code VARCHAR(255),
                                  tag_code VARCHAR(255)
);

-- contract-service

CREATE TABLE contracts (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           code CHAR(36) NOT NULL,
                           is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP,
                           requestor_code CHAR(36) NOT NULL,
                           contractor_code CHAR(36) NOT NULL,
                           freelancer_code CHAR(36) NOT NULL,
                           started_at TIMESTAMP NOT NULL,
                           ended_at TIMESTAMP NOT NULL,
                           payment_type VARCHAR(255) NOT NULL,
                           unit_amount BIGINT NOT NULL,
                           status VARCHAR(255) NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           body TEXT NOT NULL
);

CREATE TABLE deposits (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          member_code CHAR(36) NOT NULL,
                          code CHAR(36) NOT NULL,
                          amount BIGINT NOT NULL
);

CREATE TABLE deposit_histories (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   code CHAR(36) NOT NULL,
                                   is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                   created_at TIMESTAMP NOT NULL,
                                   updated_at TIMESTAMP,
                                   deposit_code CHAR(36) NOT NULL,
                                   change_amount BIGINT NOT NULL,
                                   summary VARCHAR(255) NOT NULL,
                                   result_amount BIGINT NOT NULL
);

CREATE TABLE settlements (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             code CHAR(36) NOT NULL,
                             receiver_code CHAR(36) NOT NULL,
                             contract_code CHAR(36) NOT NULL,
                             original_amount BIGINT NOT NULL,
                             settled_amount BIGINT,
                             status VARCHAR(255) NOT NULL,
                             progressing_at TIMESTAMP NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             settled_at TIMESTAMP,
                             settlement_rate DECIMAL(5, 2)
);

-- member-service




-- profile-service

CREATE TABLE experiences (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             code VARCHAR(255) NOT NULL UNIQUE,
                             is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP,
                             resume_code VARCHAR(36) NOT NULL,
                             title VARCHAR(255) NOT NULL,
                             organization VARCHAR(255) NOT NULL,
                             description TEXT NOT NULL,
                             started_at TIMESTAMP NOT NULL,
                             ended_at TIMESTAMP
);

CREATE TABLE ratings (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         receiver_code VARCHAR(36) NOT NULL UNIQUE,
                         satisfied_count INT NOT NULL DEFAULT 0,
                         unsatisfied_count INT NOT NULL DEFAULT 0
);

CREATE TABLE resumes (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(255) NOT NULL UNIQUE,
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP,
                         member_code VARCHAR(36) NOT NULL,
                         title VARCHAR(255),
                         body TEXT,
                         link VARCHAR(512)
);

CREATE TABLE self_promotions (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 code VARCHAR(255) NOT NULL UNIQUE,
                                 is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP,
                                 member_code VARCHAR(36) NOT NULL,
                                 title VARCHAR(255) NOT NULL,
                                 content TEXT NOT NULL,
                                 payment_type VARCHAR(20) NOT NULL,
                                 unit_amount BIGINT NOT NULL,
                                 resume_code VARCHAR(36)
);

CREATE TABLE members_tags (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              member_code VARCHAR(36) NOT NULL,
                              tag_code VARCHAR(36) NOT NULL,
                              UNIQUE (member_code, tag_code)
);

CREATE TABLE tags (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      skill VARCHAR(100) NOT NULL UNIQUE,
                      code VARCHAR(36) NOT NULL UNIQUE
);


CREATE TABLE members (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(36) NOT NULL UNIQUE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         phone_number VARCHAR(255) NOT NULL,
                         birth_date DATE NOT NULL,
                         gender VARCHAR(255) NOT NULL,
                         provider VARCHAR(255) NOT NULL,
                         provider_id VARCHAR(255) NOT NULL,
                         can_work BOOLEAN NOT NULL
);

CREATE TABLE social_members (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                code VARCHAR(36) NOT NULL UNIQUE,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL,
                                is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                email VARCHAR(255) NOT NULL UNIQUE,
                                provider VARCHAR(255) NOT NULL,
                                provider_id VARCHAR(255) NOT NULL
);

-- payment-service

CREATE TABLE payments (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          code VARCHAR(255) NOT NULL UNIQUE,
                          is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP,
                          order_pg_id VARCHAR(255) NOT NULL UNIQUE,
                          payment_key VARCHAR(255),
                          amount BIGINT NOT NULL,
                          payment_status VARCHAR(255) NOT NULL,
                          method VARCHAR(255) NOT NULL,  -- **추가된 컬럼: 결제 수단 (예: Card, Transfer)**
                          approve_at TIMESTAMP  -- **추가된 컬럼: 결제 승인 시간**
);

CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        code VARCHAR(255) NOT NULL UNIQUE,
                        is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP,
                        member_code VARCHAR(255) NOT NULL,
                        order_pg_id VARCHAR(255) NOT NULL UNIQUE
);