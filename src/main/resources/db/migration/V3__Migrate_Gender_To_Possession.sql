-- Idempotent migration to migrate 'gender' values to 'possession' and drop the 'gender' column
-- Safe for PostgreSQL: checks for existence of column before attempting updates/drops

DO $$
BEGIN
    -- If the gender column exists, migrate values
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name='valuations' AND column_name='gender'
    ) THEN
        -- Ensure possession column exists (if not, add it)
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name='valuations' AND column_name='possession'
        ) THEN
            ALTER TABLE valuations ADD COLUMN possession VARCHAR(50);
        END IF;

        -- Map known gender values to possession values
        UPDATE valuations
        SET possession = CASE
            WHEN gender = 'MALE' THEN 'Ready'
            WHEN gender = 'FEMALE' THEN 'Under Construction'
            ELSE possession
        END
        WHERE gender IS NOT NULL;

        -- If you also want to set a default for NULL possessions, you can uncomment below
        -- UPDATE valuations SET possession = 'Ready' WHERE possession IS NULL;

        -- Drop the old gender column
        ALTER TABLE valuations DROP COLUMN IF EXISTS gender;
    END IF;
END
$$;
