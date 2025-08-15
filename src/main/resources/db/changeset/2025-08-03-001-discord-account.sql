--liquibase formatted sql
--changeset statuxia:2025-08-03-001-discord-account

CREATE TABLE IF NOT EXISTS discord_account (
    id BIGSERIAL,
    session_token varchar(64) NOT NULL,
    access_token varchar(64) NOT NULL,
    refresh_token varchar(64) NOT NULL,
    update_time timestamp without time zone NOT NULL,
    disabled boolean default false,
    disabled_time timestamp without time zone,

    CONSTRAINT discord_account_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS discord_account_update_time_idx ON discord_account(update_time);
CREATE INDEX IF NOT EXISTS discord_account_session_token_idx ON discord_account(session_token);