-- liquibase formatted sql
-- changeset statuxia:2026-03-09-002-minigames-account-activity

CREATE TABLE IF NOT EXISTS minigames_account_activity (
    id BIGSERIAL,
    game_account_id BIGINT NOT NULL,
    activity_at TIMESTAMP WITHOUT TIME ZONE,
    type VARCHAR(64),

    CONSTRAINT minigames_account_activity_pkey PRIMARY KEY (id)
);