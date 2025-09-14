-- Migration script to add user_id column to expenses table
-- This script should be run if you have existing expense data

-- Step 1: Add the user_id column as nullable first
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS user_id INTEGER;

-- Step 2: Add foreign key constraint
ALTER TABLE expenses 
ADD CONSTRAINT IF NOT EXISTS fk_expense_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- Step 3: Update existing expenses to belong to the first user (if any exist)
-- WARNING: This assigns all existing expenses to the first user in the system
-- You may want to modify this based on your specific requirements
UPDATE expenses 
SET user_id = (SELECT id FROM users ORDER BY id LIMIT 1)
WHERE user_id IS NULL;

-- Step 4: Make the user_id column NOT NULL after assigning values
ALTER TABLE expenses ALTER COLUMN user_id SET NOT NULL;

-- Verify the migration
SELECT 
    e.id, 
    e.expense, 
    e.expense_amount, 
    e.user_id,
    u.username 
FROM expenses e 
JOIN users u ON e.user_id = u.id 
LIMIT 5;
