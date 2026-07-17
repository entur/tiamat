-- Add dtype discriminator column for Hibernate SINGLE_TABLE inheritance on Parking.
-- FintrafficParking extends Parking and is compiled into the same jar, so Hibernate
-- always requires this column regardless of which Spring profiles are active.
-- PostgreSQL 11+ handles NOT NULL DEFAULT without a table rewrite.
ALTER TABLE parking ADD COLUMN IF NOT EXISTS dtype VARCHAR(31) NOT NULL DEFAULT 'Parking';
