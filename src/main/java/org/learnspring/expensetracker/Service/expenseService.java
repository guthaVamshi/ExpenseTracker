package org.learnspring.expensetracker.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.learnspring.expensetracker.Model.Expense;
import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.repo.expenseRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class expenseService {
    
    private static final Logger logger = LoggerFactory.getLogger(expenseService.class);
    
    @Autowired
    private expenseRepo expenseRepo;

    @Transactional(readOnly = true)
    public List<Expense> getExpenses(){
        logger.debug("Retrieving all expenses from database");
        List<Expense> expenses = expenseRepo.findAll();
        logger.debug("Found {} expenses", expenses.size());
        return expenses;
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUser(Users user){
        logger.debug("Retrieving expenses for user: {}", user.getUsername());
        List<Expense> expenses = expenseRepo.findByUser(user);
        logger.debug("Found {} expenses for user: {}", expenses.size(), user.getUsername());
        return expenses;
    }

    @Transactional
    public Expense addExpense(Expense exp) {
        logger.debug("Saving expense to database: {}", exp);
        try {
            Expense savedExpense = expenseRepo.save(exp);
            logger.debug("Successfully saved expense with ID: {}", savedExpense.getId());
            return savedExpense;
        } catch (Exception e) {
            logger.error("Error saving expense: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updateExpenses(Expense exp) {
        logger.debug("Updating expense in database: {}", exp);
        try {
            expenseRepo.save(exp);
            logger.debug("Successfully updated expense with ID: {}", exp.getId());
        } catch (Exception e) {
            logger.error("Error updating expense: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteExpenses(Expense exp) {
        logger.debug("Deleting expense from database: {}", exp);
        try {
            expenseRepo.delete(exp);
            logger.debug("Successfully deleted expense with ID: {}", exp.getId());
        } catch (Exception e) {
            logger.error("Error deleting expense: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<Expense> getByMonth(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth); // expects YYYY-MM
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        logger.debug("Fetching expenses between {} and {}", start, end);
        return expenseRepo.findByDateBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<Expense> getByMonthForUser(String yearMonth, Users user) {
        YearMonth ym = YearMonth.parse(yearMonth); // expects YYYY-MM
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        logger.debug("Fetching expenses for user {} between {} and {}", user.getUsername(), start, end);
        return expenseRepo.findByUserAndDateBetween(user, start, end);
    }

    @Transactional(readOnly = true)
    public Expense getExpenseById(Integer id) {
        logger.debug("Retrieving expense with ID: {}", id);
        return expenseRepo.findById(id).orElse(null);
    }
}
