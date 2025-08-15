--liquibase formatted sql
--changeset statuxia:2025-08-10-002-token-limitation-and-account

CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL,
    discord_account BIGSERIAL NOT NULL,
    disabled boolean default false,
    disabled_time timestamp without time zone,

    CONSTRAINT account_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS account_discord_account_idx ON account(discord_account);

CREATE TABLE IF NOT EXISTS token_limitation (
    token varchar(64) NOT NULL UNIQUE,
    rate_limit INT NOT NULL,
    rate_reset_seconds INT NOT NULL,

    CONSTRAINT token_limitation_token_pkey PRIMARY KEY (token)
);
