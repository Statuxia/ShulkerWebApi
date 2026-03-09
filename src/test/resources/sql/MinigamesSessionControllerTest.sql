insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', '', '', NOW(), false, null),
(2, '', '', '', NOW(), false, null)
ON conflict do nothing;

insert into game_account
(id, name, discord_id, paid)
values
(1, 'player-one', 1, false),
(2, 'player-two', 2, false)
ON conflict do nothing;

insert into custom_token
(id, token, disabled, disabled_time)
values
(1, 'custom-token-1', false, null),
(2, 'no-authority-token-1', false, null)
ON conflict do nothing;

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('custom-token-1', 120, 1),
('no-authority-token-1', 120, 1)
ON conflict do nothing;

insert into token_authority
(token, authority)
values
('custom-token-1', 'MINIGAMES_SESSION')
ON conflict do nothing;

SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('custom_token_id_seq', (SELECT MAX(id) FROM custom_token));