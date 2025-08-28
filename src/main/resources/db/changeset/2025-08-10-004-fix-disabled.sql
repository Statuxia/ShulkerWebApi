--liquibase formatted sql
--changeset statuxia:2025-08-10-004-fix-disabled

ALTER TABLE account ALTER COLUMN disabled SET DEFAULT false, ALTER COLUMN disabled SET NOT NULL;
ALTER TABLE custom_token ALTER COLUMN disabled SET DEFAULT false, ALTER COLUMN disabled SET NOT NULL;
ALTER TABLE discord_account ALTER COLUMN disabled SET DEFAULT false, ALTER COLUMN disabled SET NOT NULL;