--liquibase formatted sql
--changeset statuxia:2025-09-07-card-logs

CREATE TABLE IF NOT EXISTS bank_card_log (
     id BIGSERIAL,
     history_uuid UUID NOT NULL,
     bank_card_id BIGSERIAL NOT NULL,
     action_by varchar(24) NOT NULL,
     action_time timestamp without time zone NOT NULL,
     log_data jsonb,

     CONSTRAINT bank_card_log_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS bank_card_log_bank_card_id_idx ON bank_card_log(bank_card_id);
