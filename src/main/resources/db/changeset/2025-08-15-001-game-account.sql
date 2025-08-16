--liquibase formatted sql
--changeset statuxia:2025-08-15-001-game-account

CREATE TABLE IF NOT EXISTS game_account (
    id BIGSERIAL,
    name varchar(20) NOT NULL,
    discord_id BIGSERIAL NOT NULL,

    CONSTRAINT game_account_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS game_account_name_idx ON game_account(name);
CREATE INDEX IF NOT EXISTS game_account_discord_id_idx ON game_account(discord_id);