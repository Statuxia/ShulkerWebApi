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

insert into token_authority
(token, authority)
values
('session-token-1', 'UNLINK_ACCOUNT'),
('session-token-1', 'LINK_ACCOUNT');

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));

INSERT INTO game_account
(id, name, discord_id, paid)
VALUES
(1000000, 'TestAccount', 1, false);