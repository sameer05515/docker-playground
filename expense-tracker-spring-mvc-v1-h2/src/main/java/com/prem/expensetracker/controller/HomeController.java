package com.prem.expensetracker.controller;

import com.prem.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

@Controller
public class HomeController {

    private final ExpenseService expenseService;

    public HomeController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/")
    public String home(Model model) {
        LocalDate now = LocalDate.now();

        model.addAttribute("totalExpenses", expenseService.getTotal());
        model.addAttribute("monthlyExpenses",
                expenseService.getMonthlyTotal(now.getMonthValue(), now.getYear()));
        model.addAttribute("expenseCount", expenseService.findAll().size());

        return "index";
    }
}
