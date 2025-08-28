--liquibase formatted sql
--changeset statuxia:2025-08-24-001-table-fixes

ALTER TABLE custom_token ALTER COLUMN account_id SET NOT NULL;
ALTER TABLE discord_account ALTER COLUMN session_token SET DEFAULT '';
