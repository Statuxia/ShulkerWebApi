INSERT INTO fine (
    id, game_account_id, fine_value, message, create_date, status, status_date, due_date, action_by, notified
)
VALUES
    (1, 1, 100, 'hello world', NOW(), 'NEW', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', false),
    (2, 1, 100, 'hello world 2', NOW(), 'PAYED', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', false),
    (3, 1, 100, 'hello world 3', NOW(), 'OVERDUE', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', false),
    (4, 1, 100, 'hello world 4', NOW(), 'CLOSED', NOW(), NOW() + INTERVAL '1 DAY', 'admin-name-1', false);

SELECT setval('fine_id_seq', (SELECT MAX(id) FROM fine));
SELECT setval('fine_log_id_seq', (SELECT MAX(id) FROM fine_log));
