--liquibase formatted sql
--changeset statuxia:2025-08-16-001-fix-authorities

ALTER TABLE token_authority
DROP CONSTRAINT token_authority_pkey,
ADD CONSTRAINT token_authority_pkey PRIMARY KEY (token, authority);