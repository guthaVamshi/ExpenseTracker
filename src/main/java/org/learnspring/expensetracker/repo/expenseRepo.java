package org.learnspring.expensetracker.repo;

import java.time.LocalDate;
import java.util.List;

import org.learnspring.expensetracker.Model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface expenseRepo extends JpaRepository<Expense, Integer> {
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
}
