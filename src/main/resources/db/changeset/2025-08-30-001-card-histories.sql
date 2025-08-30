--liquibase formatted sql
--changeset statuxia:2025-08-30-card-histories

CREATE TABLE IF NOT EXISTS bank_card_history (
     id BIGSERIAL,
     history_uuid UUID NOT NULL,
     bank_card_id BIGSERIAL NOT NULL,
     type varchar(64) NOT NULL,
     create_time timestamp without time zone NOT NULL,
     history_data jsonb,

     CONSTRAINT bank_card_history_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bank_card_operation_history (
     id BIGSERIAL,
     history_uuid UUID NOT NULL,
     bank_card_id BIGSERIAL NOT NULL,
     state varchar(24) NOT NULL default 'EXISTS',
     create_time timestamp without time zone NOT NULL,
     value int,

     CONSTRAINT bank_card_operation_history_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS bank_card_operation_history_history_uuid_idx ON bank_card_operation_history(history_uuid);
CREATE INDEX IF NOT EXISTS bank_card_operation_history_bank_card_id_idx ON bank_card_operation_history(bank_card_id);
CREATE INDEX IF NOT EXISTS bank_card_history_bank_card_id_idx ON bank_card_history(bank_card_id);
