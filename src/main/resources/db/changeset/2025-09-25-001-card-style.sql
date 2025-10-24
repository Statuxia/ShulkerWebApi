--liquibase formatted sql
--changeset statuxia:2025-09-25-card-style

CREATE TABLE IF NOT EXISTS card_style (
    type TEXT NOT NULL unique,
    card_type VARCHAR(24) NOT NULL,
    price BIGINT NOT NULL,

    CONSTRAINT card_style_pkey PRIMARY KEY(type)
);

insert into card_style
(type, card_type, price)
values
('DEFAULT', 'DIRECT', 0),
('SHULKER', 'DIRECT', 500);

ALTER TABLE bank_card ADD COLUMN card_style TEXT NOT NULL DEFAULT 'DEFAULT';
ALTER TABLE bank_card ADD COLUMN pattern_seed BIGINT NOT NULL DEFAULT 0;