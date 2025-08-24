--liquibase formatted sql
--changeset statuxia:2025-08-23-001-session-token

CREATE TABLE IF NOT EXISTS session_token (
     id BIGSERIAL,
     token varchar(64) NOT NULL UNIQUE,
     create_time timestamp without time zone NOT NULL,
     disabled boolean default false,
     disabled_time timestamp without time zone,
     account_id BIGSERIAL,

     CONSTRAINT session_token_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS session_token_token_idx ON session_token(token);
CREATE INDEX IF NOT EXISTS session_token_account_id_idx ON session_token(account_id);
CREATE INDEX IF NOT EXISTS custom_token_account_id_idx ON custom_token(account_id);
