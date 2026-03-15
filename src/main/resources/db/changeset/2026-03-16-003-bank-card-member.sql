--liquibase formatted sql
--changeset statuxia:2026-03-16-003-bank-card-member

CREATE TABLE IF NOT EXISTS bank_card_member (
    id BIGSERIAL,
    bank_card_id BIGINT NOT NULL,
    game_account_id BIGINT NOT NULL,
    pin VARCHAR(24) NOT NULL,
    added_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    credited BIGINT NOT NULL DEFAULT 0,
    debited BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT bank_card_member_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS bank_card_member_bank_card_id_idx ON bank_card_member (bank_card_id);
CREATE INDEX IF NOT EXISTS bank_card_member_game_account_id_idx ON bank_card_member (game_account_id);
