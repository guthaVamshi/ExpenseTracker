package org.learnspring.expensetracker.Model;


import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @NotBlank(message = "Expense name is required")
    @Size(max = 100, message = "Expense name must be at most 100 characters")
    private String expense;

    @NotBlank(message = "Expense type is required")
    @Size(max = 50, message = "Expense type must be at most 50 characters")
    private String expenseType;

    @NotBlank(message = "Expense amount is required")
    @Size(max = 20, message = "Expense amount must be at most 20 characters")
    private String expenseAmount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "date")
    private LocalDate date;

    @PrePersist
    public void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }
}
