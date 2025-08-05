package org.learnspring.expensetracker.Service;

import java.util.List;

import org.learnspring.expensetracker.Model.Expense;
import org.learnspring.expensetracker.repo.expenseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class expenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(expenseService.class);
    
    @Autowired
    private expenseRepo expenseRepo;

    public List<Expense> getExpenses(){
        logger.debug("Retrieving all expenses from database");
        List<Expense> expenses = expenseRepo.findAll();
        logger.debug("Found {} expenses", expenses.size());
        return expenses;
    }

    public Expense addExpense(Expense exp) {
        logger.debug("Saving expense to database: {}", exp);
        Expense savedExpense = expenseRepo.save(exp);
        logger.debug("Successfully saved expense with ID: {}", savedExpense.getId());
        return savedExpense;
    }

    public void updateExpenses(Expense exp) {
        logger.debug("Updating expense in database: {}", exp);
        expenseRepo.save(exp);
        logger.debug("Successfully updated expense with ID: {}", exp.getId());
    }

    public void deleteExpenses(Expense exp) {
        logger.debug("Deleting expense from database: {}", exp);
        expenseRepo.delete(exp);
        logger.debug("Successfully deleted expense with ID: {}", exp.getId());
    }
}
