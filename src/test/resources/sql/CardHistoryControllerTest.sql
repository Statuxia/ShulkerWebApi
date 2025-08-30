insert into bank_card_history
(id, history_uuid, bank_card_id, type, create_time, history_data)
values
(1, '00000000-0000-0000-0000-000000000001', 1, 'CREATE_CARD', NOW(), null),
(2, '00000000-0000-0000-0000-000000000002', 1, 'UPDATE_PIN', NOW(), null),
(3, '00000000-0000-0000-0000-000000000003', 1, 'DISABLE_CARD', NOW(), null),
(4, '00000000-0000-0000-0000-000000000004', 1, 'ENABLE_CARD', NOW(), null),
(5, '00000000-0000-0000-0000-000000000005', 1, 'DEPOSIT', NOW(), '{"valueChange":"0 -> 100 (+100)"}'),
(6, '00000000-0000-0000-0000-000000000006', 1, 'WITHDRAW', NOW(), '{"valueChange":"100 -> 0 (-100)"}');

insert into bank_card_operation_history
(id, history_uuid, bank_card_id, state, create_time, value)
values
(1, '00000000-0000-0000-0000-000000000005', 1, 'EXISTS', NOW(), 10),
(2, '00000000-0000-0000-0000-000000000006', 1, 'ROLLBACK', NOW(), 100);

SELECT setval('bank_card_history_id_seq', (SELECT MAX(id) FROM bank_card_history));
SELECT setval('bank_card_operation_history_id_seq', (SELECT MAX(id) FROM bank_card_operation_history));
