insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
    (1, '', 'a1', 'r1', NOW(), false, null),
    (2, '', 'a2', 'r2', NOW(), false, null),
    (3, '', 'a3', 'r3', NOW(), false, null);

insert into account (id, discord_account)
values
    (1, 1),
    (2, 2),
    (3, 3);

insert into roles
(id, role_id, name, color, discord_id, luckperms_permission, available_for_twink)
values
    (1, 'role-1', 'Role1', '#111111', 1, 'perm.1', true),
    (2, 'role-2', 'Role2', '#222222', 1, 'perm.2', true),
    (3, 'role-3', 'Role3', '#333333', 1, 'perm.3', true),
    (4, 'role-4', 'Role4', '#444444', 1, 'perm.4', true),
    (5, 'role-5', 'Role5', '#555555', 1, 'perm.5', true),
    (6, 'role-6', 'Role6', '#666666', 1, 'perm.6', true),
    (7, 'role-7', 'Role7', '#777777', 1, 'perm.7', true);

insert into account_roles
(id, account_id, role_id, receive_date, expire_date)
values
    (1, 1, 1, NOW() - interval '10 days', NOW() + interval '10 days'),
    (2, 1, 2, NOW() - interval '5 days',  NOW() + interval '5 days'),
    (3, 1, 3, NOW() - interval '20 days', NOW() - interval '1 day'),
    (4, 1, 4, NOW() - interval '15 days', NOW() - interval '2 days'),
    (5, 2, 5, NOW() - interval '3 days', NOW() + interval '30 days'),
    (6, 2, 6, NOW() - interval '4 days', NOW() + interval '40 days'),
    (7, 2, 7, NOW() - interval '2 days', NOW() + interval '20 days');

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('account_roles_id_seq', (SELECT MAX(id) FROM account_roles));
