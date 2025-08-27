package com.example.studentportal.controller;

import com.example.studentportal.model.PaymentInstallment;
import com.example.studentportal.model.PaymentTransaction;
import com.example.studentportal.model.TuitionBalance;
import com.example.studentportal.model.User;
import com.example.studentportal.service.PaymentTransactionService;
import com.example.studentportal.service.TuitionBalanceService;
import com.example.studentportal.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentBillingController {

    private final UserService userService;
    private final TuitionBalanceService balanceService;
    private final PaymentTransactionService paymentTransactionService;

    public StudentBillingController(UserService userService,
                                    TuitionBalanceService balanceService,
                                    PaymentTransactionService paymentTransactionService) {
        this.userService = userService;
        this.balanceService = balanceService;
        this.paymentTransactionService = paymentTransactionService;
    }

    @GetMapping("/tuition-balance")
    public String viewTuitionBalance(Model model, Principal principal, HttpServletResponse response) {
        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        User me = userService.findByEmail(principal.getName()).orElseThrow();
        TuitionBalance bal = balanceService.findByStudent(me).orElse(null);
        List<PaymentInstallment> schedule = balanceService.getSchedule(me);

        model.addAttribute("totalFees", bal != null ? bal.getTotalTuition() : null);
        model.addAttribute("outstandingBalance", bal != null ? bal.getOutstandingBalance() : null);
        model.addAttribute("paymentSchedule", schedule);

        PaymentInstallment next = balanceService.getNextDue(me);
        String nextDue = (next != null) ? next.getDueDate().format(DateTimeFormatter.ISO_DATE) : "N/A";
        model.addAttribute("nextDueDate", nextDue);

        // ✅ Fetch payment transactions for this student
        List<PaymentTransaction> transactions = paymentTransactionService.getPaymentsForStudent(me);
        model.addAttribute("transactions", transactions);

        return "student/tuition-balance";
    }
}
