-- STEP BY STEP PostgreSQL Migration
-- Run these commands one by one in your PostgreSQL database

-- 1. First, add the user_id column (nullable initially)
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS user_id INTEGER;

-- 2. Add foreign key constraint
ALTER TABLE expenses 
ADD CONSTRAINT IF NOT EXISTS fk_expense_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- 3. Check if you have any users
SELECT id, username FROM users;

-- 4. Assign all existing expenses to the first user
-- (Replace the '1' below with the actual user ID from step 3)
UPDATE expenses 
SET user_id = (SELECT id FROM users ORDER BY id LIMIT 1)
WHERE user_id IS NULL;

-- 5. Verify the update worked
SELECT 
    e.id, 
    e.expense, 
    e.user_id,
    u.username 
FROM expenses e 
LEFT JOIN users u ON e.user_id = u.id 
LIMIT 10;

-- 6. If everything looks good, make the column NOT NULL
-- (You can run this later after testing)
-- ALTER TABLE expenses ALTER COLUMN user_id SET NOT NULL;
