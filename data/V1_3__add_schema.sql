-- commissions_capacity
CREATE TABLE IF NOT EXISTS commissions_capacity
(
    id                 BIGINT      NOT NULL AUTO_INCREMENT,
    commission_code    VARCHAR(36) NOT NULL,
    applied_count      INT         NOT NULL,
    apply_capacity     INT         NOT NULL,
    selected_count     INT         NOT NULL,
    selection_capacity INT         NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (commission_code)
);