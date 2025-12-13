
CREATE TABLE `cart_items` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL UNIQUE,
    `contract_code` VARCHAR(255) NOT NULL,
    `cart_code` VARCHAR(255) NOT NULL,
    `status` VARCHAR(255) NOT NULL,
    `started_at` DATETIME NOT NULL,
    `ended_at` DATETIME NOT NULL,
    `payment_type` VARCHAR(255) NOT NULL,
    `amount` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `carts` (
    `Id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL UNIQUE,
    `is_deleted` BOOLEAN NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME,
    `member_code` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`Id`)
);

CREATE TABLE `commissions` (
    `Id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL UNIQUE,
    `is_deleted` BOOLEAN NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME,
    `member_code` VARCHAR(255) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` VARCHAR(255) NOT NULL,
    `payment_type` VARCHAR(255) NOT NULL,
    `unit_amount` VARCHAR(255) NOT NULL,
    `started_at` DATE NOT NULL,
    `ended_at` DATE NOT NULL,
    `is_open` BOOLEAN NOT NULL,
    `writer_name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`Id`)
);

CREATE TABLE `commissions_tags` (
    `id` VARCHAR(255) NOT NULL,
    `commission_code` VARCHAR(255) NOT NULL,
    `tag_code` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `contracts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL,
    `requestor_code` CHAR(36) NOT NULL,
    `contractor_code` CHAR(36) NOT NULL,
    `freelancer_code` CHAR(36) NOT NULL,
    `code` CHAR(36) NOT NULL,
    `started_at` DATETIME NOT NULL,
    `ended_at` DATETIME NOT NULL,
    `payment_type` VARCHAR(255) NOT NULL,
    `unit_amount` BIGINT NOT NULL,
    `status` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `body` TEXT NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `deposits` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_code` CHAR(36) NOT NULL,
    `code` CHAR(36) NOT NULL,
    `amount` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `deposit_histories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL,
    `deposit_code` CHAR(36) NOT NULL,
    `code` CHAR(36) NOT NULL,
    `change_amount` BIGINT NOT NULL,
    `summary` VARCHAR(255) NOT NULL,
    `result_amount` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `settlements` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` CHAR(36) NOT NULL,
    `receiver_code` CHAR(36) NOT NULL,
    `contract_code` CHAR(36) NOT NULL,
    `original_amount` BIGINT NOT NULL,
    `settled_amount` BIGINT,
    `status` VARCHAR(255) NOT NULL,
    `progressing_at` DATETIME NOT NULL,
    `created_at` DATETIME NOT NULL,
    `settled_at` DATETIME,
    `settlement_rate` DECIMAL(5, 2),
    PRIMARY KEY (`id`)
);

CREATE TABLE `orders` (
    `Id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL UNIQUE,
    `is_deleted` BOOLEAN NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME,
    `member_code` VARCHAR(255) NOT NULL,
    `order_pg_id` VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (`Id`)
);

CREATE TABLE `payments` (
    `Id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(255) NOT NULL UNIQUE,
    `is_deleted` BOOLEAN NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME,
    `order_pg_id` VARCHAR(255) NOT NULL UNIQUE,
    `payment_key` VARCHAR(255),
    `amount` BIGINT NOT NULL,
    `payment_status` VARCHAR(255) NOT NULL,
    `method` VARCHAR(255) NOT NULL,
    `approve_at` DATETIME,
    PRIMARY KEY (`Id`)
);

CREATE TABLE `experiences` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(36) NOT NULL UNIQUE,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL,
    `resume_code` VARCHAR(36) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `organization` VARCHAR(255) NOT NULL,
    `description` TEXT NOT NULL,
    `started_at` DATETIME NOT NULL,
    `ended_at` DATETIME,
    PRIMARY KEY (`id`)
);

CREATE TABLE `ratings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `receiver_code` VARCHAR(36) NOT NULL UNIQUE,
    `satisfied_count` INT NOT NULL,
    `unsatisfied_count` INT NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `resumes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(36) NOT NULL UNIQUE,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL,
    `member_code` VARCHAR(36) NOT NULL,
    `title` VARCHAR(255),
    `body` TEXT,
    `link` VARCHAR(512),
    PRIMARY KEY (`id`)
);

CREATE TABLE `self_promotions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(36) NOT NULL UNIQUE,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    `is_deleted` BOOLEAN NOT NULL,
    `member_code` VARCHAR(36) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NOT NULL,
    `payment_type` VARCHAR(20) NOT NULL,
    `unit_amount` BIGINT NOT NULL,
    `resume_code` VARCHAR(36),
    PRIMARY KEY (`id`)
);

CREATE TABLE `members_tags` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_code` VARCHAR(36) NOT NULL,
    `tag_code` VARCHAR(36) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_member_tag_unique` (`member_code`, `tag_code`)
);

CREATE TABLE `tags` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `skill` VARCHAR(100) NOT NULL UNIQUE,
    `code` VARCHAR(36) NOT NULL UNIQUE,
    PRIMARY KEY (`id`)
);


CREATE TABLE `members` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `code` VARCHAR(36) NOT NULL UNIQUE,
                           `created_at` DATETIME NOT NULL,
                           `updated_at` DATETIME NOT NULL,
                           `is_deleted` BOOLEAN NOT NULL,
                           `nick_name` VARCHAR(255) NOT NULL,
                           `email` VARCHAR(255) NOT NULL UNIQUE,
                           `phone_number` VARCHAR(255) NOT NULL,
                           `birth_date` DATE NOT NULL,
                           `gender` VARCHAR(255) NOT NULL,
                           `provider` VARCHAR(255) NOT NULL,
                           `provider_id` VARCHAR(255) NOT NULL,
                           `can_work` BOOLEAN NOT NULL,
                           PRIMARY KEY (`id`)
);

CREATE TABLE `social_members` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `code` VARCHAR(36) NOT NULL UNIQUE,
                                  `created_at` DATETIME NOT NULL,
                                  `updated_at` DATETIME NOT NULL,
                                  `is_deleted` BOOLEAN NOT NULL,
                                  `email` VARCHAR(255) NOT NULL UNIQUE,
                                  `provider` VARCHAR(255) NOT NULL,
                                  `provider_id` VARCHAR(255) NOT NULL,
                                  PRIMARY KEY (`id`)
);
