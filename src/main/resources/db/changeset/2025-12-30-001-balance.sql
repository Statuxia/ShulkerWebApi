-- liquibase formatted sql
-- changeset statuxia:2025-12-30-001-balance

CREATE TABLE IF NOT EXISTS game_account_balance (
    id SERIAL NOT NULL,
    game_account_id SERIAL NOT NULL,
    type varchar(32) NOT NULL,
    value INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT game_account_balance_pkey PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS game_account_balance_game_account_id_idx ON game_account_balance(game_account_id);

CREATE TABLE IF NOT EXISTS game_account_balance_history (
    id SERIAL NOT NULL,
    balance_id SERIAL NOT NULL,
    game_account_id SERIAL NOT NULL,
    type varchar(32) NOT NULL,
    data jsonb,

    create_date timestamp without time zone not null default NOW(),

    CONSTRAINT game_account_balance_history_pkey PRIMARY KEY(id)
);

CREATE INDEX IF NOT EXISTS game_account_balance_history_balance_id_idx ON game_account_balance_history(balance_id);
CREATE INDEX IF NOT EXISTS game_account_balance_history_game_account_id_idx ON game_account_balance_history(game_account_id);
