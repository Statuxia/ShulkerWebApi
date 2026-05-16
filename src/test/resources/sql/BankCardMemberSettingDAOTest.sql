insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null);

insert into account
(id, discord_account)
values
(1, 1);

insert into game_account
(id, name, discord_id)
values
(1, 'player-one', 1),
(2, 'player-two', 1);

insert into bank_card
(id, number, game_account_id, pin, type)
values
(1, '1111 2222', 1, '1111', 'GROUP'),
(2, '3333 4444', 1, '2222', 'GROUP');

insert into bank_card_member
(id, bank_card_id, game_account_id, pin, added_at, credited, debited)
values
(1, 1, 1, '$2b$12$GBVR7nmnHOj6cAMfboG8oegA6JODLz.F84j3AihuC6X31q787puOK', NOW(), 0, 0),
(2, 1, 2, '$2b$12$wp9SwTiNR3luptWJfjoUNeOM5WVHeslxZ293NALQc7fygckDAtVUK', NOW(), 0, 0);

insert into bank_card_member_setting
(id, bank_card_id, bank_card_member_id, type, value)
values
(1, 1, 1, 'DEPOSIT_PERMISSION', null),
(2, 1, 1, 'WITHDRAW_PERMISSION', null),
(3, 1, 2, 'DEPOSIT_PERMISSION', null),
(4, 1, 2, 'TRANSFER_PERMISSION', null);

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('bank_card_id_seq', (SELECT MAX(id) FROM bank_card));
SELECT setval('bank_card_member_id_seq', (SELECT MAX(id) FROM bank_card_member));
SELECT setval('bank_card_member_setting_id_seq', (SELECT MAX(id) FROM bank_card_member_setting));
