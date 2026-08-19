-- V1: initial schema
-- Flyway applies this migration once; never edit an applied migration.
-- Add new changes in V2__*.sql, V3__*.sql, etc.

CREATE TABLE IF NOT EXISTS items (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

COMMENT ON TABLE  items            IS 'Sample domain entity — replace with your schema.';
COMMENT ON COLUMN items.name       IS 'Display name of the item.';
COMMENT ON COLUMN items.created_at IS 'UTC timestamp when the row was inserted.';
