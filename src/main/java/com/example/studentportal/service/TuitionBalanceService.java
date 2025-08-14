package com.example.studentportal.service;

import com.example.studentportal.model.PaymentInstallment;
import com.example.studentportal.model.TuitionBalance;
import com.example.studentportal.model.User;
import com.example.studentportal.repository.PaymentInstallmentRepository;
import com.example.studentportal.repository.TuitionBalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TuitionBalanceService {

    private final TuitionBalanceRepository balanceRepo;
    private final PaymentInstallmentRepository installmentRepo;

    public TuitionBalanceService(TuitionBalanceRepository balanceRepo,
                                 PaymentInstallmentRepository installmentRepo) {
        this.balanceRepo = balanceRepo;
        this.installmentRepo = installmentRepo;
    }

    @Transactional(readOnly = true)
    public Optional<TuitionBalance> findByStudent(User student) {
        return balanceRepo.findByStudent(student);
    }

    @Transactional
    public TuitionBalance setOrUpdateTuition(User student, BigDecimal newTotalTuition) {
        TuitionBalance b = balanceRepo.findByStudent(student).orElseGet(() -> {
            TuitionBalance nb = new TuitionBalance();
            nb.setStudent(student);
            return nb;
        });
        b.setTotalTuition(newTotalTuition);
        TuitionBalance saved = balanceRepo.save(b);

        // If there is no schedule yet, generate a default 4-part monthly schedule
        List<PaymentInstallment> existing = installmentRepo.findByStudentOrderByDueDateAsc(student);
        if (existing.isEmpty() && newTotalTuition != null && newTotalTuition.compareTo(BigDecimal.ZERO) > 0) {
            generateDefaultSchedule(student, newTotalTuition, 4);
        }
        return saved;
    }

    /** Split tuition into equal installments (rounded to cents); due monthly starting next month 1st. */
    @Transactional
    public void generateDefaultSchedule(User student, BigDecimal total, int parts) {
        installmentRepo.findByStudentOrderByDueDateAsc(student).forEach(installmentRepo::delete);

        BigDecimal[] divRem = total.divideAndRemainder(new BigDecimal(parts));
        BigDecimal base = divRem[0];
        BigDecimal remainder = divRem[1]; // in currency cents this is fine if total has 2 decimals

        LocalDate firstDue = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        for (int i = 0; i < parts; i++) {
            PaymentInstallment pi = new PaymentInstallment();
            pi.setStudent(student);
            pi.setDueDate(firstDue.plusMonths(i));
            BigDecimal amount = base;
            if (i == parts - 1) amount = base.add(remainder); // add remainder to last installment
            pi.setAmount(amount);
            installmentRepo.save(pi);
        }
    }

    @Transactional
    public TuitionBalance addToPaid(User student, BigDecimal delta) {
        TuitionBalance b = balanceRepo.findByStudent(student).orElseThrow();
        b.setAmountPaid(b.getAmountPaid().add(delta));
        return balanceRepo.save(b);
    }

    @Transactional
    public TuitionBalance subtractFromPaid(User student, BigDecimal delta) {
        TuitionBalance b = balanceRepo.findByStudent(student).orElseThrow();
        b.setAmountPaid(b.getAmountPaid().subtract(delta));
        return balanceRepo.save(b);
    }

    @Transactional(readOnly = true)
    public List<PaymentInstallment> getSchedule(User student) {
        return installmentRepo.findByStudentOrderByDueDateAsc(student);
    }

    @Transactional(readOnly = true)
    public PaymentInstallment getNextDue(User student) {
        return installmentRepo.findFirstByStudentAndPaidFalseOrderByDueDateAsc(student);
    }

    @Transactional
    public void applyPaymentToInstallments(User student, BigDecimal payment) {
        if (payment == null || payment.compareTo(BigDecimal.ZERO) <= 0) return;
        List<PaymentInstallment> open = installmentRepo.findByStudentAndPaidFalseOrderByDueDateAsc(student);
        for (PaymentInstallment pi : open) {
            if (payment.compareTo(BigDecimal.ZERO) <= 0) break;
            BigDecimal before = payment;
            pi.applyPayment(payment);
            // compute how much was actually consumed
            BigDecimal consumed = before.subtract(paymentRemaining(pi, before));
            payment = payment.subtract(consumed);
            installmentRepo.save(pi);
        }
    }

    /** Helper to compute remaining after applying hypothetical payment to a single installment */
    private BigDecimal paymentRemaining(PaymentInstallment pi, BigDecimal incoming) {
        BigDecimal remaining = pi.getAmount().subtract(pi.getAmountPaid());
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) return incoming;
        BigDecimal toApply = incoming.min(remaining);
        return incoming.subtract(toApply);
    }
}
