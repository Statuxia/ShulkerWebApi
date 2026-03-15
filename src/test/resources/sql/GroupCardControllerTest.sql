insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null),
(2, '', 'access-token-2', 'refresh-token-2', NOW(), false, null);

insert into account
(id, discord_account)
values
(1, 1),
(2, 2);

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('session-token-1', 120, 1),
('session-token-2', 120, 1);

insert into session_token
(id, token, account_id, create_time)
values
(1, 'session-token-1', 1, NOW()),
(2, 'session-token-2', 2, NOW());

insert into game_account
(id, name, discord_id)
values
(1, 'owner', 1),
(2, 'member-one', 1),
(3, 'other-player', 2);

insert into bank_card
(id, number, game_account_id, pin, type, name)
values
(1, '1111 0000', 1, '1234', 'GROUP', 'Test Group Card'),
(2, '2222 0000', 2, '4321', 'DIRECT', null);

insert into bank_card_member
(id, bank_card_id, game_account_id, pin, added_at, credited, debited)
values
(1, 1, 2, '0001', NOW(), 0, 0);

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('bank_card_id_seq', (SELECT MAX(id) FROM bank_card));
SELECT setval('bank_card_member_id_seq', (SELECT MAX(id) FROM bank_card_member));
