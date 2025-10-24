insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', '', '', NOW(), false, null),
(2, '', '', '', NOW(), false, null)
;

insert into account
(id, discord_account)
values
(1, 1),
(2, 2)
;

insert into session_token
(id, token, account_id, create_time)
values
(1, 'session-token-1', 1, NOW()),
(2, 'admin-token-1', 2, NOW())
;

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('session-token-1', 120, 1),
('admin-token-1', 120, 1)
;

insert into token_authority
(token, authority)
values
('admin-token-1', 'CREATE_FINE'),
('admin-token-1', 'EDIT_MESSAGE_FINE'),
('admin-token-1', 'CLOSE_FINE'),
('admin-token-1', 'LIST_FINE'),
('admin-token-1', 'PAY_FINE');

insert into game_account
(id, name, discord_id)
values
(1, 'test-name', 1),
(2, 'admin-name-1', 2)
;

insert into bank_card
(id, number, game_account_id, pin, type, currency)
values
(1, '1234 5678', 1, '1234', 'DIRECT', 500000),
(2, '1111 1111', 2, '1234', 'DIRECT', 500)
;

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('bank_card_id_seq', (SELECT MAX(id) FROM bank_card));
