package org.learnspring.expensetracker.Service;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.learnspring.expensetracker.Model.Expense;
import org.learnspring.expensetracker.repo.expenseRepo;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private expenseRepo expenseRepo;

    @InjectMocks
    private expenseService expenseService;

    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testExpense = new Expense();
        testExpense.setId(1);
        testExpense.setExpense("Groceries");
        testExpense.setExpenseType("Food");
        testExpense.setExpenseAmount("50.00");
    }

    @Test
    @DisplayName("Should return all expenses when getExpenses is called")
    void getExpenses_ShouldReturnAllExpenses() {
        // Arrange
        List<Expense> expectedExpenses = Arrays.asList(testExpense);
        when(expenseRepo.findAll()).thenReturn(expectedExpenses);

        // Act
        List<Expense> actualExpenses = expenseService.getExpenses();

        // Assert
        assertNotNull(actualExpenses);
        assertEquals(1, actualExpenses.size());
        assertEquals("Groceries", actualExpenses.get(0).getExpense());
        verify(expenseRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no expenses exist")
    void getExpenses_ShouldReturnEmptyList_WhenNoExpenses() {
        // Arrange
        when(expenseRepo.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Expense> actualExpenses = expenseService.getExpenses();

        // Assert5
        assertNotNull(actualExpenses);
        assertTrue(actualExpenses.isEmpty());
        verify(expenseRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should save and return expense when addExpense is called")
    void addExpense_ShouldSaveAndReturnExpense() {
        // Arrange
        Expense expenseToSave = new Expense();
        expenseToSave.setExpense("Coffee");
        expenseToSave.setExpenseType("Food");
        expenseToSave.setExpenseAmount("5.00");

        Expense savedExpense = new Expense();
        savedExpense.setId(2);
        savedExpense.setExpense("Coffee");
        savedExpense.setExpenseType("Food");
        savedExpense.setExpenseAmount("5.00");

        when(expenseRepo.save(any(Expense.class))).thenReturn(savedExpense);

        // Act
        Expense result = expenseService.addExpense(expenseToSave);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Coffee", result.getExpense());
        verify(expenseRepo, times(1)).save(expenseToSave);
    }

    @Test
    @DisplayName("Should update expense when updateExpenses is called")
    void updateExpenses_ShouldUpdateExpense() {
        // Arrange
        Expense expenseToUpdate = new Expense();
        expenseToUpdate.setId(1);
        expenseToUpdate.setExpense("Updated Groceries");
        expenseToUpdate.setExpenseType("Food");
        expenseToUpdate.setExpenseAmount("75.00");

        when(expenseRepo.save(any(Expense.class))).thenReturn(expenseToUpdate);

        // Act
        expenseService.updateExpenses(expenseToUpdate);

        // Assert
        verify(expenseRepo, times(1)).save(expenseToUpdate);
    }

    @Test
    @DisplayName("Should delete expense when deleteExpenses is called")
    void deleteExpenses_ShouldDeleteExpense() {
        // Arrange
        Expense expenseToDelete = new Expense();
        expenseToDelete.setId(1);
        expenseToDelete.setExpense("To Delete");
        expenseToDelete.setExpenseType("Misc");
        expenseToDelete.setExpenseAmount("10.00");

        doNothing().when(expenseRepo).delete(any(Expense.class));

        // Act
        expenseService.deleteExpenses(expenseToDelete);

        // Assert
        verify(expenseRepo, times(1)).delete(expenseToDelete);
    }

    @Test
    @DisplayName("Should handle null expense gracefully")
    void addExpense_ShouldHandleNullExpense() {
        // Arrange
        when(expenseRepo.save(null)).thenThrow(new IllegalArgumentException("Expense cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.addExpense(null);
        });
    }
} 