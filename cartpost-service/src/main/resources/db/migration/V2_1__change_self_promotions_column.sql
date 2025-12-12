ALTER TABLE self_promotions
    ADD UNIQUE INDEX uk_member_code (member_code);

ALTER TABLE self_promotions
    ADD COLUMN writerName VARCHAR(255) NOT NULL DEFAULT 'UNKNOWN' AFTER title;