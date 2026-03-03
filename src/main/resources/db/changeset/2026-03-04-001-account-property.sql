-- liquibase formatted sql
-- changeset statuxia:2026-03-04-001-account-property

CREATE TABLE IF NOT EXISTS account_property (
    id BIGSERIAL,
    account_id BIGSERIAL NOT NULL,
    name VARCHAR(255) NOT NULL,
    value TEXT,

    CONSTRAINT account_property_pkey PRIMARY KEY (id)
);
