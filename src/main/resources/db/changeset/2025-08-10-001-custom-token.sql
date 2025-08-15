--liquibase formatted sql
--changeset statuxia:2025-08-10-001-custom-token

CREATE TABLE IF NOT EXISTS custom_token (
    id BIGSERIAL,
    token varchar(64) NOT NULL UNIQUE,
    disabled boolean default false,
    disabled_time timestamp without time zone,

    CONSTRAINT custom_token_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS custom_token_token_idx ON custom_token(token);
