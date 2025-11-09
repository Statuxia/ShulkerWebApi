-- liquibase formatted sql
-- changeset statuxia:2025-11-08-002-session-ip

CREATE TABLE IF NOT EXISTS game_session_ip (
    id BIGSERIAL,
    game_account_id BIGSERIAL NOT NULL,
    ip varchar(32) NOT NULL,
    last_join_date timestamp without time zone not null default NOW(),
    state varchar(32) NOT NULL,
    notified boolean NOT NULL DEFAULT false,

    CONSTRAINT game_session_ip_pkey PRIMARY KEY (id)
);
