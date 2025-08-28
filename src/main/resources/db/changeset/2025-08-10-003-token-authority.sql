--liquibase formatted sql
--changeset statuxia:2025-08-10-003-token-authority

CREATE TABLE IF NOT EXISTS token_authority (
    token varchar(64) NOT NULL,
    authority varchar(64) NOT NULL,

    CONSTRAINT token_authority_pkey PRIMARY KEY (token)
);

CREATE INDEX IF NOT EXISTS token_authority_token_idx ON token_authority(token);

ALTER TABLE custom_token ADD COLUMN IF NOT EXISTS account_id BIGSERIAL;
