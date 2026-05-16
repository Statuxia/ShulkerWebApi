insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null),
(2, '', 'access-token-2', 'refresh-token-2', NOW(), false, null);

insert into account
(id, discord_account)
values
(1, 1),
(2, 2);

insert into game_account
(id, name, discord_id)
values
(1, 'player-one', 1),
(2, 'player-two', 1),
(3, 'player-three', 2);

insert into bank_card
(id, number, game_account_id, pin, type)
values
(1, '1111 2222', 1, '1111', 'GROUP'),
(2, '3333 4444', 2, '2222', 'GROUP');

insert into bank_card_member
(id, bank_card_id, game_account_id, pin, added_at, credited, debited)
values
(1, 1, 1, '$2b$12$GBVR7nmnHOj6cAMfboG8oegA6JODLz.F84j3AihuC6X31q787puOK', '2026-01-01 10:00:00', 100, 50),
(2, 1, 2, '$2b$12$wp9SwTiNR3luptWJfjoUNeOM5WVHeslxZ293NALQc7fygckDAtVUK', '2026-01-15 10:00:00', 200, 100),
(3, 2, 1, '$2b$12$Izna6MuG7k0Ne8DUwoEcyu2PgpJZq5OnncerVJwtpePyXF7YllLU.', '2026-03-10 10:00:00', 0, 0),
(4, 2, 3, '0004', '2026-03-10 12:00:00', 300, 150);

SELECT setval('account_id_seq', (SELECT MAX(id) FROM account));
SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('bank_card_id_seq', (SELECT MAX(id) FROM bank_card));
SELECT setval('bank_card_member_id_seq', (SELECT MAX(id) FROM bank_card_member));
