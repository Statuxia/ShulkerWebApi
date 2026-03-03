insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null);

insert into account
(id, discord_account)
values
(1, 1),
(2, null);

insert into account_property
(id, account_id, name, value)
values
(1, 1, 'SOME_PROPERTY', 'value-1'),
(2, 1, 'ANOTHER_PROPERTY', 'value-2'),
(3, 1, 'SOME_PROPERTY', 'value-duplicate'),
(4, 2, 'SOME_PROPERTY', 'value-other-account');

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('account_property_id_seq', (SELECT MAX(id) FROM account_property));