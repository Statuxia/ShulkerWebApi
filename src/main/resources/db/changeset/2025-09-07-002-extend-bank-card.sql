--liquibase formatted sql
--changeset statuxia:2025-09-07-extend-bank-card

ALTER TABLE bank_card ADD COLUMN IF NOT EXISTS create_time timestamp without time zone NOT NULL default NOW();
