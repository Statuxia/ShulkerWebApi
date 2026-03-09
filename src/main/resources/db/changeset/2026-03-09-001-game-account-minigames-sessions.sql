-- liquibase formatted sql
-- changeset statuxia:2026-03-09-001-game-account-minigames-sessions

CREATE TABLE IF NOT EXISTS game_account_minigames_sessions (
    id BIGSERIAL,
    game_account_id BIGINT NOT NULL,
    playtime BIGINT NOT NULL DEFAULT 0,
    first_join TIMESTAMP WITHOUT TIME ZONE,
    last_join TIMESTAMP WITHOUT TIME ZONE,
    online BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT game_account_minigames_sessions_pkey PRIMARY KEY (id)
);