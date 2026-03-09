-- liquibase formatted sql
-- changeset statuxia:2026-03-09-001-minigames-account-session

CREATE TABLE IF NOT EXISTS minigames_account_session_sessions (
    id BIGSERIAL,
    game_account_id BIGINT NOT NULL,
    playtime BIGINT NOT NULL DEFAULT 0,
    first_join TIMESTAMP WITHOUT TIME ZONE,
    last_join TIMESTAMP WITHOUT TIME ZONE,
    online BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT minigames_account_session_pkey PRIMARY KEY (id)
);