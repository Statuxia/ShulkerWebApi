insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
    (1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null);

insert into account
(id, discord_account)
values
    (1, 1);

insert into session_token
(id, token, account_id, create_time)
values
    (1, 'session-token-1', 1, NOW());

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
    ('session-token-1', 120, 1);

insert into token_authority
(token, authority)
values
    ('session-token-1', 'ACCOUNT_ROLE_GET_FOR_BATCH');

insert into session_token
(id, token, account_id, create_time)
values
    (2, 'admin-token-1', 1, NOW());

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
    ('admin-token-1', 120, 1);

insert into token_authority
(token, authority)
values
    ('admin-token-1', 'ACCOUNT_ROLE_GET_FOR_BATCH');

insert into roles
(id, role_id, name, color, discord_id, luckperms_permission, available_for_twink)
values
    (1, 'discord-role-1', 'Premium', '#FFD700', 1, 'group.premium', true),
    (2, 'discord-role-2', 'VIP', '#00FFFF', 1, 'group.vip', true);

insert into account_roles
(id, account_id, role_id, receive_date, expire_date)
values
    (1, 1, 1, NOW(), NOW() + interval '30 days');

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('account_roles_id_seq', (SELECT MAX(id) FROM account_roles));
SELECT setval('session_token_id_seq', (SELECT MAX(id) FROM session_token));
