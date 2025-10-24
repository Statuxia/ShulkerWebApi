--liquibase formatted sql
--changeset statuxia:2025-10-23-001-add-fines

CREATE TABLE IF NOT EXISTS fine (
     id BIGSERIAL,
     game_account_id BIGSERIAL,
     fine_value BIGINT NOT NULL,
     message TEXT NOT NULL,
     create_date timestamp without time zone,
     status varchar(32) NOT NULL,
     status_date timestamp without time zone,
     due_date timestamp without time zone,
     action_by varchar(20) NOT NULL,
     notified boolean NOT NULL default false,

     CONSTRAINT fine_pkey PRIMARY KEY (id)
 );

CREATE INDEX IF NOT EXISTS fine_game_account_id_idx ON fine(game_account_id);
CREATE INDEX IF NOT EXISTS fine_status_idx ON fine(status);

CREATE TABLE IF NOT EXISTS fine_log (
     id BIGSERIAL,
     fine_id BIGSERIAL,
     action_by varchar(20) NOT NULL,
     fine_action varchar(32) NOT NULL,
     fine_data jsonb,
     log_date timestamp without time zone,

     CONSTRAINT fine_log_pkey PRIMARY KEY (id)
 );

CREATE INDEX IF NOT EXISTS fine_log_fine_id_idx ON fine_log(fine_id);
