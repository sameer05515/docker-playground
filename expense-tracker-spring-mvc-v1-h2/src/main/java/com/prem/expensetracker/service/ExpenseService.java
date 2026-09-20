package com.prem.expensetracker.service;

import com.prem.expensetracker.entity.Expense;
import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {
    List<Expense> findAll();
    Expense findById(Long id);
    Expense save(Expense expense);
    void deleteById(Long id);
    BigDecimal getMonthlyTotal(int month, int year);
    BigDecimal getTotal();
}
