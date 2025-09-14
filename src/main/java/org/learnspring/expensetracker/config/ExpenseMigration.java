package org.learnspring.expensetracker.config;

import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMigration implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseMigration.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if migration is needed
            boolean migrationNeeded = checkIfMigrationNeeded();
            
            if (migrationNeeded) {
                logger.info("Starting expense migration to assign user_id to existing expenses...");
                
                // Get the first user to assign to existing expenses
                Users firstUser = userRepo.findAll().stream().findFirst().orElse(null);
                
                if (firstUser != null) {
                    // Update existing expenses that don't have a user_id
                    int updatedRows = jdbcTemplate.update(
                        "UPDATE expenses SET user_id = ? WHERE user_id IS NULL", 
                        firstUser.getId()
                    );
                    
                    logger.info("Migration completed! Updated {} expenses to belong to user: {}", 
                               updatedRows, firstUser.getUsername());
                } else {
                    logger.warn("No users found in database. Please create a user first.");
                }
            } else {
                logger.info("No migration needed - all expenses already have user assignments.");
            }
        } catch (Exception e) {
            logger.error("Migration failed: {}", e.getMessage());
            // Don't fail the application startup, just log the error
        }
    }

    private boolean checkIfMigrationNeeded() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM expenses WHERE user_id IS NULL", 
                Integer.class
            );
            return count != null && count > 0;
        } catch (Exception e) {
            // If the column doesn't exist yet, Hibernate will create it
            logger.debug("Could not check for migration need (column might not exist yet): {}", e.getMessage());
            return false;
        }
    }
}
