-- liquibase formatted sql
-- changeset feeland:2026-02-14-001-add-account-roles

CREATE TABLE IF NOT EXISTS account_roles (
    id BIGSERIAL,
    account_id BIGSERIAL NOT NULL,
    role_id BIGSERIAL NOT NULL,
    receive_date timestamp without time zone NOT NULL,
    expire_date timestamp without time zone NOT NULL,

    CONSTRAINT account_roles_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS account_roles_account_id_idx ON account_roles(account_id);