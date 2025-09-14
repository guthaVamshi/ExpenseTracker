-- PostgreSQL Migration Script
-- Run this directly in your PostgreSQL database

-- First, let's check if the column already exists
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'expenses' AND column_name = 'user_id'
    ) THEN
        -- Add the user_id column as nullable first
        ALTER TABLE expenses ADD COLUMN user_id INTEGER;
        
        -- Add foreign key constraint to users table
        ALTER TABLE expenses 
        ADD CONSTRAINT fk_expense_user 
        FOREIGN KEY (user_id) REFERENCES users(id);
        
        -- Assign all existing expenses to the first user (if any exist)
        -- You can modify this query if you want different assignment logic
        UPDATE expenses 
        SET user_id = (SELECT id FROM users ORDER BY id LIMIT 1)
        WHERE user_id IS NULL;
        
        -- Now make the column NOT NULL
        ALTER TABLE expenses ALTER COLUMN user_id SET NOT NULL;
        
        RAISE NOTICE 'Migration completed successfully!';
    ELSE
        RAISE NOTICE 'Column user_id already exists, skipping migration.';
    END IF;
END $$;

-- Verify the migration
SELECT 
    COUNT(*) as total_expenses,
    COUNT(user_id) as expenses_with_user
FROM expenses;

-- Show some sample data
SELECT 
    e.id, 
    e.expense, 
    e.expense_amount, 
    e.user_id,
    u.username 
FROM expenses e 
LEFT JOIN users u ON e.user_id = u.id 
LIMIT 5;
