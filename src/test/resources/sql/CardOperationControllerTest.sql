insert into token_authority
(token, authority)
values
('session-token-1', 'HISTORY_ROLLBACK_OPERATION'),
('session-token-1', 'HISTORY_RESTORE_OPERATION');

insert into bank_card_history
(id, history_uuid, bank_card_id, type, create_time, history_data)
values
(1, '00000000-0000-0000-0000-000000000001', 1, 'DEPOSIT', NOW(), '{"valueChange":"-100 -> 0 (+100)"}'),
(2, '00000000-0000-0000-0000-000000000002', 1, 'DEPOSIT', NOW(), '{"valueChange":"-100 -> 0 (+100)"}'),
(3, '00000000-0000-0000-0000-000000000003', 1, 'WITHDRAW', NOW(), '{"valueChange":"100 -> 0 (-100)"}'),
(4, '00000000-0000-0000-0000-000000000004', 1, 'WITHDRAW', NOW(), '{"valueChange":"100 -> 0 (-100)"}');

insert into bank_card_operation_history
(id, history_uuid, bank_card_id, state, create_time, value)
values
(1, '00000000-0000-0000-0000-000000000001', 1, 'EXISTS', NOW(), 100),
(2, '00000000-0000-0000-0000-000000000002', 1, 'ROLLBACK', NOW(), 100),
(3, '00000000-0000-0000-0000-000000000003', 1, 'EXISTS', NOW(), -100),
(4, '00000000-0000-0000-0000-000000000004', 1, 'ROLLBACK', NOW(), -100);

SELECT setval('bank_card_history_id_seq', (SELECT MAX(id) FROM bank_card_history));
SELECT setval('bank_card_operation_history_id_seq', (SELECT MAX(id) FROM bank_card_operation_history));