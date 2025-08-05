package org.learnspring.expensetracker.repo;

import org.learnspring.expensetracker.Model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface expenseRepo extends JpaRepository<Expense, Integer> {
}
