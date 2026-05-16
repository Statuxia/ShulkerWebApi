--liquibase formatted sql
--changeset statuxia:2026-03-16-001-bank-card-group

ALTER TABLE bank_card ADD COLUMN IF NOT EXISTS name VARCHAR(64);
