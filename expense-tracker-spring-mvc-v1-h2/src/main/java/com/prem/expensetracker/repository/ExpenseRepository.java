package com.prem.expensetracker.repository;

import com.prem.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByOrderByExpenseDateDesc();

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE MONTH(e.expenseDate) = :month
          AND YEAR(e.expenseDate) = :year
    """)
    BigDecimal getMonthlyTotal(@Param("month") int month,
                               @Param("year") int year);
}
