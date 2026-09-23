package com.prem.expensetracker.service;

import com.prem.expensetracker.entity.Expense;
import com.prem.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseServiceImpl(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<Expense> findAll() {
        return repository.findAllByOrderByExpenseDateDesc();
    }

    public Expense findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
    }

    public Expense save(Expense expense) {
        return repository.save(expense);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public BigDecimal getMonthlyTotal(int month, int year) {
        return repository.getMonthlyTotal(month, year);
    }

    public BigDecimal getTotal() {
        return repository.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
