--liquibase formatted sql
--changeset statuxia:2025-10-17-remove-shulker-style runOnChange:true


TRUNCATE TABLE card_style;

insert into card_style
(type, card_type, price)
values
('DEFAULT', 'DIRECT', 0),
('SUNSET', 'DIRECT', 64),
('FOREST', 'DIRECT', 64),
('LAVENDER', 'DIRECT', 64),
('CORAL', 'DIRECT', 64),
('MINT', 'DIRECT', 64),
('NEON', 'DIRECT', 64),
('NATURE', 'DIRECT', 64),
('OCEAN', 'DIRECT', 64),
('FIRE', 'DIRECT', 644),
('ROYAL', 'DIRECT', 64),
('STEEL', 'DIRECT', 64),
('ANONIMUS', 'DIRECT', 500),
('LIGHTNING', 'DIRECT', 5000)
;
