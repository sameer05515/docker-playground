package com.prem.expensetracker.controller;

import com.prem.expensetracker.entity.Expense;
import com.prem.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private static final String[] CATEGORIES =
            {"Food", "Travel", "Shopping", "Bills", "Health", "Entertainment", "Other"};

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("expenses", expenseService.findAll());
        return "expenses/list";
    }

    @GetMapping("/new")
    public String newExpense(Model model) {
        Expense expense = new Expense();
        expense.setExpenseDate(LocalDate.now());
        model.addAttribute("expense", expense);
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("pageTitle", "Add Expense");
        return "expenses/form";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("expense") Expense expense,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", CATEGORIES);
            model.addAttribute("pageTitle",
                    expense.getId() == null ? "Add Expense" : "Edit Expense");
            return "expenses/form";
        }

        expenseService.save(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("expense", expenseService.findById(id));
        model.addAttribute("categories", CATEGORIES);
        model.addAttribute("pageTitle", "Edit Expense");
        return "expenses/form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        expenseService.deleteById(id);
        return "redirect:/expenses";
    }
}
