package org.learnspring.expensetracker.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.learnspring.expensetracker.Model.Expense;
import org.learnspring.expensetracker.Model.Users;
import org.learnspring.expensetracker.Service.expenseService;
import org.learnspring.expensetracker.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private expenseService service;

    @Autowired
    private UserRepo userRepo;

    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepo.findByUsername(username);
    }

    private boolean isExpenseOwnedByUser(Integer expenseId, Users user) {
        Expense expense = service.getExpenseById(expenseId);
        return expense != null && expense.getUser() != null && expense.getUser().getId().equals(user.getId());
    }

    @GetMapping("/")
    public Map<String, String> indexPage(){
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ExpenseTracker API");
        response.put("message", "Welcome to Expense Tracker");
        response.put("timestamp", java.time.Instant.now().toString());
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health(){
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "ExpenseTracker");
        health.put("timestamp", java.time.Instant.now().toString());
        return health;
    }

    @GetMapping("/test-auth")
    public String testAuth() {
        logger.info("Test auth endpoint called - authentication successful");
        return "Authentication successful";
    }

    @GetMapping("/all")
    public List<Expense> getAllExpenses(){
        Users currentUser = getCurrentUser();
        logger.info("Fetching all expenses for user: {}", currentUser.getUsername());
        return service.getExpensesByUser(currentUser);
    }

    @GetMapping("/by-month/{yearMonth}")
    public List<Expense> getByMonth(@PathVariable String yearMonth){
        Users currentUser = getCurrentUser();
        logger.info("Fetching expenses for month {} for user: {}", yearMonth, currentUser.getUsername());
        return service.getByMonthForUser(yearMonth, currentUser);
    }

    @PostMapping("/add")
    public Expense addExpenses(@Valid @RequestBody Expense exp){
        Users currentUser = getCurrentUser();
        exp.setUser(currentUser);
        logger.info("Adding new expense: {} for user: {}", exp, currentUser.getUsername());
        try {
            Expense savedExpense = service.addExpense(exp);
            logger.info("Successfully added expense with ID: {} for user: {}", savedExpense.getId(), currentUser.getUsername());
            return savedExpense;
        } catch (Exception e) {
            logger.error("Failed to add expense for user: {} - Error: {}", currentUser.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to save expense. Please try again.", e);
        }
    }
    @GetMapping("/CsrfToken")
   public CsrfToken getCsrfToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");

   }
    @PutMapping("/updateExpense")
    public Expense updateExpenses(@Valid @RequestBody Expense exp){
        Users currentUser = getCurrentUser();
        
        try {
            // Check if the expense belongs to the current user
            if (!isExpenseOwnedByUser(exp.getId(), currentUser)) {
                logger.warn("User {} attempted to update expense {} owned by another user", 
                           currentUser.getUsername(), exp.getId());
                throw new RuntimeException("You can only update your own expenses");
            }
            
            // Ensure the user is set correctly (prevent user switching)
            exp.setUser(currentUser);
            logger.info("Updating expense with ID: {} for user: {}", exp.getId(), currentUser.getUsername());
            service.updateExpenses(exp);
            logger.info("Successfully updated expense with ID: {} for user: {}", exp.getId(), currentUser.getUsername());
            return exp;
        } catch (Exception e) {
            logger.error("Failed to update expense {} for user: {} - Error: {}", exp.getId(), currentUser.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to update expense. Please try again.", e);
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteExpenses(@PathVariable Integer id){
        Users currentUser = getCurrentUser();
        
        try {
            // Check if the expense belongs to the current user
            if (!isExpenseOwnedByUser(id, currentUser)) {
                logger.warn("User {} attempted to delete expense {} owned by another user", 
                           currentUser.getUsername(), id);
                throw new RuntimeException("You can only delete your own expenses");
            }
            
            logger.info("Deleting expense with ID: {} for user: {}", id, currentUser.getUsername());
            Expense exp = new Expense();
            exp.setId(id);
            service.deleteExpenses(exp);
            logger.info("Successfully deleted expense with ID: {} for user: {}", id, currentUser.getUsername());
            return "Expense with ID " + id + " deleted successfully";
        } catch (Exception e) {
            logger.error("Failed to delete expense {} for user: {} - Error: {}", id, currentUser.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to delete expense. Please try again.", e);
        }
    }

    /**
     * API Documentation endpoint
     * Returns a simple JSON documentation of all available endpoints
     */
    @GetMapping("/api-docs")
    public Map<String, Object> getApiDocumentation() {
        Map<String, Object> apiDocs = new HashMap<>();
        
        apiDocs.put("title", "Expense Tracker API");
        apiDocs.put("version", "1.0");
        apiDocs.put("description", "REST API for managing personal expenses");
        
        Map<String, Object> endpoints = new HashMap<>();
        
        // GET /
        Map<String, Object> welcome = new HashMap<>();
        welcome.put("method", "GET");
        welcome.put("path", "/");
        welcome.put("description", "Welcome endpoint - returns welcome message");
        welcome.put("response", "String");
        endpoints.put("welcome", welcome);
        
        // GET /all
        Map<String, Object> getAll = new HashMap<>();
        getAll.put("method", "GET");
        getAll.put("path", "/all");
        getAll.put("description", "Get all expenses");
        getAll.put("response", "List<Expense>");
        endpoints.put("getAllExpenses", getAll);

        // GET /by-month/{yearMonth}
        Map<String, Object> byMonth = new HashMap<>();
        byMonth.put("method", "GET");
        byMonth.put("path", "/by-month/{yearMonth}");
        byMonth.put("description", "Get expenses for a given month (YYYY-MM)");
        byMonth.put("pathVariable", "yearMonth (String)");
        byMonth.put("response", "List<Expense>");
        endpoints.put("getByMonth", byMonth);
        
        // POST /add
        Map<String, Object> add = new HashMap<>();
        add.put("method", "POST");
        add.put("path", "/add");
        add.put("description", "Create a new expense");
        add.put("requestBody", "Expense object (JSON)");
        add.put("response", "Expense");
        add.put("validation", "All fields are required");
        endpoints.put("addExpense", add);
        
        // PUT /updateExpense
        Map<String, Object> update = new HashMap<>();
        update.put("method", "PUT");
        update.put("path", "/updateExpense");
        update.put("description", "Update an existing expense");
        update.put("requestBody", "Expense object with ID (JSON)");
        update.put("response", "Expense");
        update.put("validation", "All fields are required");
        endpoints.put("updateExpense", update);
        
        // DELETE /delete/{id}
        Map<String, Object> delete = new HashMap<>();
        delete.put("method", "DELETE");
        delete.put("path", "/delete/{id}");
        delete.put("description", "Delete an expense by ID");
        delete.put("pathVariable", "id (Integer)");
        delete.put("response", "String");
        endpoints.put("deleteExpense", delete);
        
        apiDocs.put("endpoints", endpoints);
        
        // Expense model structure
        Map<String, Object> expenseModel = new HashMap<>();
        expenseModel.put("id", "Integer (auto-generated)");
        expenseModel.put("expense", "String (required, max 100 chars)");
        expenseModel.put("expenseType", "String (required, max 50 chars)");
        expenseModel.put("expenseAmount", "String (required, max 20 chars)");
        apiDocs.put("expenseModel", expenseModel);
        
        // Example request
        Map<String, Object> example = new HashMap<>();
        example.put("expense", "Groceries");
        example.put("expenseType", "Food");
        example.put("expenseAmount", "50.00");
        apiDocs.put("exampleRequest", example);
        
        return apiDocs;
    }
}
