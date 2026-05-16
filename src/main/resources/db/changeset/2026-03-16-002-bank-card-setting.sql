--liquibase formatted sql
--changeset statuxia:2026-03-16-002-bank-card-setting

CREATE TABLE IF NOT EXISTS bank_card_setting (
    id BIGSERIAL,
    bank_card_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    value VARCHAR(64) NOT NULL,

    CONSTRAINT bank_card_setting_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS bank_card_setting_bank_card_id_idx ON bank_card_setting (bank_card_id);
