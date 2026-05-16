insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null);

insert into account
(id, discord_account)
values
(1, 1);

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('session-token-1', 120, 1);

insert into session_token
(id, token, account_id, create_time)
values
(1, 'session-token-1', 1, NOW());

insert into game_account
(id, name, discord_id)
values
(1, 'test-name', 1),
(2, 'test-name-paid', 1);

insert into token_authority
(token, authority)
values
('session-token-1', 'ADD_GAME_ACCOUNTS'),
('session-token-1', 'CAN_CREATE_TWINK')
;

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
