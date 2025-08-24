--liquibase formatted sql
--changeset statuxia:2025-08-19-001-bank-card

CREATE TABLE IF NOT EXISTS bank_card(
    id BIGSERIAL,
    number INTEGER NOT NULL UNIQUE,
    game_account_id BIGSERIAL,
    pin INTEGER NOT NULL,
    currency BIGINT NOT NULL DEFAULT 0,
    type VARCHAR(24) NOT NULL,
    disabled boolean default false,
    disabled_time timestamp without time zone,

    CONSTRAINT bank_card_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS bank_card_game_account_id_idx ON bank_card(game_account_id);
