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
(2, 'admin-token-1', 2, NOW()),
(3, 'admin-token-2', 2, NOW())
;

insert into token_limitation
(token, rate_limit, rate_reset_seconds)
values
('session-token-1', 120, 1),
('admin-token-1', 120, 1),
('admin-token-2', 120, 1)
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
(1, '1234 5678', 1, '$2b$12$aaRzrXQpn/Sn5VGtvM5amOAzn5XQOvuaVsfQUsmudBKSj/8944fJe', 'DIRECT', 500000),
(2, '1111 1111', 2, '$2b$12$aaRzrXQpn/Sn5VGtvM5amOAzn5XQOvuaVsfQUsmudBKSj/8944fJe', 'DIRECT', 500)
;

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('bank_card_id_seq', (SELECT MAX(id) FROM bank_card));


INSERT INTO fine (
    id, game_account_id, fine_value, message, create_date, status, status_date, due_date, action_by, notified
)
VALUES
    (1, 1, 100, 'hello world', NOW(), 'NEW', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', false),
    (2, 1, 200, 'hello world', NOW(), 'NEW', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-2', true),
    (3, 1, 300, 'hello world', NOW(), 'NEW', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-3', false),
    (4, 1, 400, 'hello world', NOW(), 'NEW', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-4', true),
    (12, 1, 200, 'hello world 2', NOW(), 'PAYED', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', true),
    (13, 1, 300, 'hello world 2', NOW(), 'PAYED', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', true),
    (14, 1, 400, 'hello world 2', NOW(), 'PAYED', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', true),
    (23, 1, 300, 'hello world 3', NOW(), 'OVERDUE', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', false),
    (24, 1, 400, 'hello world 3', NOW(), 'OVERDUE', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', true),
    (34, 1, 400, 'hello world 4', NOW(), 'CLOSED', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', true);

SELECT setval('fine_id_seq', (SELECT MAX(id) FROM fine));
SELECT setval('fine_log_id_seq', (SELECT MAX(id) FROM fine_log));
