package org.learnspring.expensetracker.repo;

import java.time.LocalDate;
import java.util.List;

import org.learnspring.expensetracker.Model.Expense;
import org.learnspring.expensetracker.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface expenseRepo extends JpaRepository<Expense, Integer> {
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    List<Expense> findByUser(Users user);
    List<Expense> findByUserAndDateBetween(Users user, LocalDate start, LocalDate end);
}
