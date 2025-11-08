-- liquibase formatted sql
-- changeset statuxia:2025-11-08-001-paid-accounts

CREATE TABLE IF NOT EXISTS paid_account (
    id BIGSERIAL,
    name varchar(20) NOT NULL,

    CONSTRAINT paid_account_pkey PRIMARY KEY (id)
);

ALTER TABLE game_account ADD COLUMN IF NOT EXISTS paid boolean NOT NULL default false;