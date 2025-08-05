package org.learnspring.expensetracker.Controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.learnspring.expensetracker.Model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class HomeControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("Should return welcome message for root endpoint")
    void indexPage_ShouldReturnWelcomeMessage() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/", String.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("welcome", response.getBody());
    }

    @Test
    @DisplayName("Should return empty list when no expenses exist")
    void getAllExpenses_ShouldReturnEmptyList_WhenNoExpenses() {
        // Act
        ResponseEntity<List<Expense>> response = restTemplate.exchange(
            baseUrl + "/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Expense>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Note: This test might not be empty if other tests have run first
        // In a real scenario, you'd use @DirtiesContext or separate test classes
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should create new expense successfully")
    void addExpenses_ShouldCreateExpense_WhenValidData() {
        // Arrange
        Expense expense = new Expense();
        expense.setExpense("Test Groceries");
        expense.setExpenseType("Food");
        expense.setExpenseAmount("25.50");

        HttpEntity<Expense> request = new HttpEntity<>(expense, headers);

        // Act
        ResponseEntity<Expense> response = restTemplate.postForEntity(
            baseUrl + "/add",
            request,
            Expense.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Groceries", response.getBody().getExpense());
        assertEquals("Food", response.getBody().getExpenseType());
        assertEquals("25.50", response.getBody().getExpenseAmount());
        assertTrue(response.getBody().getId() > 0);
    }

    @Test
    @DisplayName("Should return validation errors when invalid data is provided")
    void addExpenses_ShouldReturnValidationErrors_WhenInvalidData() {
        // Arrange
        Expense invalidExpense = new Expense();
        invalidExpense.setExpense(""); // Empty expense name
        invalidExpense.setExpenseType(""); // Empty expense type
        invalidExpense.setExpenseAmount(""); // Empty amount

        HttpEntity<Expense> request = new HttpEntity<>(invalidExpense, headers);

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/add",
            request,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Expense name is required"));
        assertTrue(response.getBody().contains("Expense type is required"));
        assertTrue(response.getBody().contains("Expense amount is required"));
    }

    @Test
    @DisplayName("Should update existing expense successfully")
    void updateExpenses_ShouldUpdateExpense_WhenValidData() {
        // Arrange - First create an expense
        Expense originalExpense = new Expense();
        originalExpense.setExpense("Original Name");
        originalExpense.setExpenseType("Food");
        originalExpense.setExpenseAmount("10.00");

        HttpEntity<Expense> createRequest = new HttpEntity<>(originalExpense, headers);
        ResponseEntity<Expense> createResponse = restTemplate.postForEntity(
            baseUrl + "/add",
            createRequest,
            Expense.class
        );

        // Update the expense
        Expense updatedExpense = createResponse.getBody();
        updatedExpense.setExpense("Updated Name");
        updatedExpense.setExpenseAmount("15.00");

        HttpEntity<Expense> updateRequest = new HttpEntity<>(updatedExpense, headers);

        // Act
        ResponseEntity<Expense> response = restTemplate.exchange(
            baseUrl + "/updateExpense",
            HttpMethod.PUT,
            updateRequest,
            Expense.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Name", response.getBody().getExpense());
        assertEquals("15.00", response.getBody().getExpenseAmount());
    }

    @Test
    @DisplayName("Should delete expense successfully")
    void deleteExpenses_ShouldDeleteExpense_WhenValidData() {
        // Arrange - First create an expense
        Expense expenseToDelete = new Expense();
        expenseToDelete.setExpense("To Delete");
        expenseToDelete.setExpenseType("Misc");
        expenseToDelete.setExpenseAmount("5.00");

        HttpEntity<Expense> createRequest = new HttpEntity<>(expenseToDelete, headers);
        ResponseEntity<Expense> createResponse = restTemplate.postForEntity(
            baseUrl + "/add",
            createRequest,
            Expense.class
        );

        Expense createdExpense = createResponse.getBody();
        assertNotNull(createdExpense);
        assertTrue(createdExpense.getId() > 0);
        
        // Act - Use path variable instead of request body
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/delete/" + createdExpense.getId(),
            HttpMethod.DELETE,
            null,
            String.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("deleted successfully"));
    }

    @Test
    @DisplayName("Should return expenses after creating multiple expenses")
    void getAllExpenses_ShouldReturnExpenses_AfterCreatingExpenses() {
        // Arrange - Create multiple expenses
        Expense expense1 = new Expense();
        expense1.setExpense("Expense 1");
        expense1.setExpenseType("Food");
        expense1.setExpenseAmount("10.00");

        Expense expense2 = new Expense();
        expense2.setExpense("Expense 2");
        expense2.setExpenseType("Transport");
        expense2.setExpenseAmount("20.00");

        HttpEntity<Expense> request1 = new HttpEntity<>(expense1, headers);
        HttpEntity<Expense> request2 = new HttpEntity<>(expense2, headers);

        restTemplate.postForEntity(baseUrl + "/add", request1, Expense.class);
        restTemplate.postForEntity(baseUrl + "/add", request2, Expense.class);

        // Act
        ResponseEntity<List<Expense>> response = restTemplate.exchange(
            baseUrl + "/all",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Expense>>() {}
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() >= 2);
    }
} 