# User Isolation Implementation

This document describes the changes made to implement user-specific expense access, ensuring that logged-in users can only access their own transactions.

## Changes Made

### 1. Database Schema Changes

#### Expense Model (`/src/main/java/org/learnspring/expensetracker/Model/Expense.java`)
- Added `@ManyToOne` relationship with `Users` entity
- Added `user_id` foreign key column
- Each expense is now associated with a specific user

#### Repository Updates (`/src/main/java/org/learnspring/expensetracker/repo/expenseRepo.java`)
- Added `findByUser(Users user)` method
- Added `findByUserAndDateBetween(Users user, LocalDate start, LocalDate end)` method

### 2. Service Layer Updates

#### Expense Service (`/src/main/java/org/learnspring/expensetracker/Service/expenseService.java`)
- Added `getExpensesByUser(Users user)` method
- Added `getByMonthForUser(String yearMonth, Users user)` method
- Added `getExpenseById(Integer id)` method for authorization checks

### 3. Controller Layer Updates

#### Home Controller (`/src/main/java/org/learnspring/expensetracker/Controllers/HomeController.java`)
- Added `getCurrentUser()` helper method to get authenticated user
- Added `isExpenseOwnedByUser()` method for authorization checks
- Updated all endpoints to filter by current user:
  - `GET /all` - Returns only current user's expenses
  - `GET /by-month/{yearMonth}` - Returns current user's expenses for specified month
  - `POST /add` - Automatically assigns new expenses to current user
  - `PUT /updateExpense` - Only allows updating own expenses
  - `DELETE /delete/{id}` - Only allows deleting own expenses

### 4. Security Features

- **Authorization Checks**: Update and delete operations verify expense ownership
- **Automatic User Assignment**: New expenses are automatically assigned to the authenticated user
- **Access Control**: Users cannot access, modify, or delete expenses belonging to other users
- **Error Handling**: Proper error messages when unauthorized access is attempted

## Database Migration

### For New Installations
If this is a fresh installation, Hibernate will automatically create the correct schema with the `user_id` column.

### For Existing Installations
If you have existing expense data, run the migration script:

```sql
-- Run the migration.sql file
\i migration.sql
```

Or execute the commands manually:

```sql
-- Add user_id column
ALTER TABLE expenses ADD COLUMN IF NOT EXISTS user_id INTEGER;

-- Add foreign key constraint
ALTER TABLE expenses 
ADD CONSTRAINT IF NOT EXISTS fk_expense_user 
FOREIGN KEY (user_id) REFERENCES users(id);

-- Assign existing expenses to first user (modify as needed)
UPDATE expenses 
SET user_id = (SELECT id FROM users ORDER BY id LIMIT 1)
WHERE user_id IS NULL;

-- Make user_id required
ALTER TABLE expenses ALTER COLUMN user_id SET NOT NULL;
```

## API Changes

### Before
- All endpoints returned all expenses regardless of user
- No authorization checks on CRUD operations

### After
- All endpoints now filter by authenticated user
- Authorization checks prevent cross-user access
- Automatic user assignment for new expenses

### API Endpoints Behavior

1. **GET /all** - Returns only the authenticated user's expenses
2. **GET /by-month/{yearMonth}** - Returns authenticated user's expenses for the specified month
3. **POST /add** - Creates expense for the authenticated user
4. **PUT /updateExpense** - Updates expense only if owned by authenticated user
5. **DELETE /delete/{id}** - Deletes expense only if owned by authenticated user

## Testing the Changes

1. **Start the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Create multiple users** using the existing registration endpoint

3. **Login as different users** and verify:
   - Each user only sees their own expenses
   - Users cannot modify expenses created by others
   - New expenses are automatically assigned to the logged-in user

## Security Considerations

- All CRUD operations are now user-scoped
- Spring Security context is used to identify the current user
- Database-level foreign key constraints ensure data integrity
- Authorization checks prevent unauthorized access attempts

## Impact on Frontend

The frontend application should continue to work without changes, as the API contract remains the same. However, the behavior will now be user-specific:
- The same API endpoints will return different data based on the authenticated user
- Users will only see and manage their own expenses
