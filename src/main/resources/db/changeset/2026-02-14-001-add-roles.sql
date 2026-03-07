-- liquibase formatted sql
-- changeset feeland:2026-02-14-001-add-roles

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL,
    role_id varchar(64) NOT NULL,
    name varchar(64) NOT NULL,
    color varchar(12),
    discord_id BIGSERIAL NOT NULL,
    luckperms_permission varchar(64),
    available_for_twink boolean NOT NULL default false,

    CONSTRAINT roles_pkey PRIMARY KEY (id)
);
