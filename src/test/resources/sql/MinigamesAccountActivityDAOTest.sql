insert into discord_account
(id, session_token, access_token, refresh_token, update_time, disabled, disabled_time)
values
(1, '', 'access-token-1', 'refresh-token-1', NOW(), false, null),
(2, '', 'access-token-2', 'refresh-token-2', NOW(), false, null);

insert into game_account
(id, name, discord_id, paid)
values
(1, 'player-one', 1, false),
(2, 'player-two', 2, false);

insert into minigames_account_activity
(id, game_account_id, activity_at, type)
values
(1, 1, '2026-01-01 10:00:00', 'JOIN_SERVER'),
(2, 1, '2026-01-02 11:00:00', 'JOIN_SERVER'),
(3, 1, '2026-01-03 12:00:00', 'LEFT_SERVER'),
(4, 2, '2026-01-04 13:00:00', 'JOIN_SERVER');

SELECT setval('game_account_id_seq', (SELECT MAX(id) FROM game_account));
SELECT setval('minigames_account_activity_id_seq', (SELECT MAX(id) FROM minigames_account_activity));