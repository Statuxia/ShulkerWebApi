--liquibase formatted sql
--changeset statuxia:2026-03-16-004-bank-card-member-setting

CREATE TABLE IF NOT EXISTS bank_card_member_setting (
    id BIGSERIAL,
    bank_card_id BIGINT NOT NULL,
    bank_card_member_id BIGINT NOT NULL,
    type VARCHAR(64) NOT NULL,
    value VARCHAR(64),

    CONSTRAINT bank_card_member_setting_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS bank_card_member_setting_bank_card_id_idx ON bank_card_member_setting (bank_card_id);
CREATE INDEX IF NOT EXISTS bank_card_member_setting_bank_card_member_id_idx ON bank_card_member_setting (bank_card_member_id);
