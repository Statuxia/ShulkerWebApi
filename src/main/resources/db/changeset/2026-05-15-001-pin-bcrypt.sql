--liquibase formatted sql
--changeset statuxia:2026-05-15-001-pin-bcrypt

ALTER TABLE bank_card ALTER COLUMN pin TYPE VARCHAR(60);
ALTER TABLE bank_card_member ALTER COLUMN pin TYPE VARCHAR(60);
