package com.example.studentportal.controller;

import com.example.studentportal.model.PaymentTransaction;
import com.example.studentportal.model.TuitionBalance;
import com.example.studentportal.model.User;
import com.example.studentportal.model.Subject;
import com.example.studentportal.service.PaymentTransactionService;
import com.example.studentportal.service.TuitionBalanceService;
import com.example.studentportal.service.UserService;
import com.example.studentportal.service.SubjectService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cashier")
public class CashierController {

    private final UserService userService;
    private final TuitionBalanceService balanceService;
    private final PaymentTransactionService txService;
    private final SubjectService subjectService;

    public CashierController(UserService userService,
                             TuitionBalanceService balanceService,
                             PaymentTransactionService txService,
                             SubjectService subjectService) {
        this.userService = userService;
        this.balanceService = balanceService;
        this.txService = txService;
        this.subjectService = subjectService;
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

        // Calculate tuition based on subjects separated by semester
        double ratePerUnit = 2000.00; // Set your rate per unit here
        double firstSemesterTuition = 0.00;
        double secondSemesterTuition = 0.00;
        double totalCalculatedTuition = 0.00;

        List<Subject> firstSemesterSubjects = new ArrayList<>();
        List<Subject> secondSemesterSubjects = new ArrayList<>();
        List<Subject> otherSemesterSubjects = new ArrayList<>();

        if (student.getCourse() != null) {
            List<Subject> allSubjects = subjectService.getSubjectsByCourseId(student.getCourse().getId());

            for (Subject subject : allSubjects) {
                if (subject.getUnits() != null) {
                    double subjectCost = subject.getUnits() * ratePerUnit;

                    if ("1st Sem".equalsIgnoreCase(subject.getSemester())) {
                        firstSemesterTuition += subjectCost;
                        firstSemesterSubjects.add(subject);
                    } else if ("2nd Sem".equalsIgnoreCase(subject.getSemester())) {
                        secondSemesterTuition += subjectCost;
                        secondSemesterSubjects.add(subject);
                    } else {
                        // Handle subjects without semester or with other semester values
                        totalCalculatedTuition += subjectCost;
                        otherSemesterSubjects.add(subject);
                    }
                }
            }

            totalCalculatedTuition += firstSemesterTuition + secondSemesterTuition;
        }

        model.addAttribute("student", student);
        model.addAttribute("balance", balOpt.orElse(null));
        model.addAttribute("transactions", txs);
        model.addAttribute("firstSemesterSubjects", firstSemesterSubjects);
        model.addAttribute("secondSemesterSubjects", secondSemesterSubjects);
        model.addAttribute("otherSemesterSubjects", otherSemesterSubjects);
        model.addAttribute("firstSemesterTuition", firstSemesterTuition);
        model.addAttribute("secondSemesterTuition", secondSemesterTuition);
        model.addAttribute("ratePerUnit", ratePerUnit);
        model.addAttribute("calculatedTuition", totalCalculatedTuition);

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