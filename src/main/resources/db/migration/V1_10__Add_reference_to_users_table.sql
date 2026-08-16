ALTER TABLE users
    ADD COLUMN reference VARCHAR(20);

-- Backfills any pre-existing rows so the NOT NULL constraint below can be applied.
-- Safe to remove this UPDATE if the table is guaranteed to be empty (e.g. fresh test DB).
UPDATE users u
SET reference = sub.ref
FROM (
    SELECT id, 'STD' || LPAD(ROW_NUMBER() OVER (ORDER BY id)::text, 4, '0') AS ref
    FROM users
    WHERE reference IS NULL
) sub
WHERE u.id = sub.id;

ALTER TABLE users
    ALTER COLUMN reference SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uq_users_reference UNIQUE (reference);
