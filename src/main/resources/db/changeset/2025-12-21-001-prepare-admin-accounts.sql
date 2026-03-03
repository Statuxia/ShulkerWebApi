-- liquibase formatted sql
-- changeset statuxia:2025-12-21-001-prepare-admin-accounts runOnChange=true

DELETE FROM account WHERE discord_account in (100000001, 100000002);
DELETE FROM discord_account WHERE id in (100000001, 100000002);
DELETE FROM game_account WHERE discord_id in (100000001, 100000002);
DELETE FROM bank_card WHERE number = '0000 0000';

INSERT INTO account
(id, discord_account)
VALUES
(100000001, 100000001), -- Администратор
(100000002, 100000002); -- Аккаунты для link

INSERT INTO discord_account
(id, session_token, access_token, refresh_token, update_time)
VALUES
(100000001, '', 'generated', 'generated', NOW()), -- Администратор
(100000002, '', 'generated', 'generated', NOW()); -- Аккаунты для link

INSERT INTO game_account
(id, name, discord_id, paid)
VALUES
(100000001, 'Администратор', 100000001, false);

INSERT INTO bank_card
(id, number, game_account_id, pin, currency, type, create_time, card_style, pattern_seed)
VALUES
(100000001, '0000 0000', 100000001, '0000', 0, 'ADMIN', NOW(), 'DEFAULT', 0);