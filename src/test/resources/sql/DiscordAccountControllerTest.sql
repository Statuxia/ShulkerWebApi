insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, 'session-token-1', 'access-token-1', 'refresh-token-1', NOW(), false, null);

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('session-token-1', 120, 1)