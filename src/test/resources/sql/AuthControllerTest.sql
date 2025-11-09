insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null),
(3, '', 'access-token-3', 'refresh-token-3', NOW(), false, null),
(2, '', 'access-token-2', 'refresh-token-2', NOW(), false, null);

insert into account
(id, discord_account)
values
(1, 1),
(3, 3),
(2, 2);

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('session-token-1', 120, 1),
('session-token-3', 120, 1),
('session-token-2', 120, 1);

insert into token_authority
(token, authority)
values
('session-token-1', 'AUTH_VALIDATE'),
('session-token-1', 'AUTH_QUEUE'),
('session-token-1', 'AUTH_CHANGE_STATE'),
('session-token-1', 'AUTH_REFRESH');

insert into session_token
(id, token, account_id, create_time)
values
(1, 'session-token-1', 1, NOW()),
(3, 'session-token-3', 3, NOW()),
(2, 'session-token-2', 2, NOW());

insert into game_account
(id, name, discord_id)
values
(1, 'test-name', 1),
(3, 'test-name-1', 1),
(4, 'test-name-3', 1),
(2, 'test-name-2', 2);


SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
