package com.example.studentportal.controller;

import com.example.studentportal.model.PaymentTransaction;
import com.example.studentportal.model.TuitionBalance;
import com.example.studentportal.model.User;
import com.example.studentportal.service.PaymentTransactionService;
import com.example.studentportal.service.TuitionBalanceService;
import com.example.studentportal.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    private final UserService userService;
    private final TuitionBalanceService balanceService;
    private final PaymentTransactionService txService;

    public CashierController(UserService userService,
                             TuitionBalanceService balanceService,
                             PaymentTransactionService txService) {
        this.userService = userService;
        this.balanceService = balanceService;
        this.txService = txService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // list only STUDENTS
        List<User> students = userService.findByRole("STUDENT");
        model.addAttribute("students", students);
        return "cashier/cashier-dashboard";
    }

    @GetMapping("/student/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        User student = userService.findById(id).orElseThrow();
        Optional<TuitionBalance> balOpt = balanceService.findByStudent(student);
        List<PaymentTransaction> txs = txService.listByStudent(student);

        model.addAttribute("student", student);
        model.addAttribute("balance", balOpt.orElse(null));
        model.addAttribute("transactions", txs);
        return "cashier/cashier-student";
    }

    @PostMapping("/assess/{id}")
    public String assessTuition(@PathVariable Long id,
                                @RequestParam BigDecimal totalTuition) {
        User student = userService.findById(id).orElseThrow();
        balanceService.setOrUpdateTuition(student, totalTuition);
        return "redirect:/cashier/student/" + id;
    }

    @PostMapping("/record/{id}")
    public String recordPayment(@PathVariable Long id,
                                @RequestParam BigDecimal amount,
                                @RequestParam(required = false) String method,
                                @RequestParam(required = false) String reference,
                                @RequestParam(required = false) String note,
                                Principal principal) {
        User student = userService.findById(id).orElseThrow();
        User cashier = userService.findByEmail(principal.getName()).orElse(null); // current user
        txService.record(student, cashier, amount, method, reference, note);
        return "redirect:/cashier/student/" + id;
    }

    @PostMapping("/transaction/{txId}/update")
    public String updateTransaction(@PathVariable Long txId,
                                    @RequestParam BigDecimal amount) {
        PaymentTransaction tx = txService.updateAmount(txId, amount);
        return "redirect:/cashier/student/" + tx.getStudent().getId();
    }

    @PostMapping("/transaction/{txId}/delete")
    public String deleteTransaction(@PathVariable Long txId) {
        // roll back effect from balance
        PaymentTransaction placeholder = txService.updateAmount(txId, BigDecimal.ZERO); // ensure student id
        Long studentId = placeholder.getStudent().getId();
        txService.delete(txId);
        return "redirect:/cashier/student/" + studentId;
    }
}
