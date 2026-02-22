-- liquibase formatted sql
-- changeset feeland:2026-02-22-001-add-queue-schedules

CREATE TABLE IF NOT EXISTS queue_schedules (
    id BIGSERIAL,
    type VARCHAR(50) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    completed BOOLEAN DEFAULT FALSE NOT NULL,
    complete_date timestamp without time zone,
    data JSONB,

    CONSTRAINT queue_schedules_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS queue_schedules_completed_idx ON queue_schedules(completed);
CREATE INDEX IF NOT EXISTS queue_schedules_type_idx ON queue_schedules(type);