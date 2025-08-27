package com.example.studentportal.service;

import com.example.studentportal.model.PaymentTransaction;
import com.example.studentportal.model.User;
import com.example.studentportal.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentTransactionService {

    private final PaymentTransactionRepository txRepo;
    private final TuitionBalanceService balanceService;

    public PaymentTransactionService(PaymentTransactionRepository txRepo,
                                     TuitionBalanceService balanceService) {
        this.txRepo = txRepo;
        this.balanceService = balanceService;
    }

    @Transactional(readOnly = true)
    public List<PaymentTransaction> listByStudent(User student) {
        return txRepo.findByStudentOrderByRecordedAtDesc(student);
    }

    /** Record a payment (record only) and update balance + apply to oldest unpaid installments */
    @Transactional
    public PaymentTransaction record(User student, User cashier, BigDecimal amount, String semester, String method, String reference, String note) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setStudent(student);
        tx.setRecordedBy(cashier);
        tx.setAmount(amount);
        tx.setSemester(semester);
        tx.setMethod(method);
        tx.setReference(reference);
        tx.setNote(note);
        PaymentTransaction saved = txRepo.save(tx);

        balanceService.addToPaid(student, amount);
        balanceService.applyPaymentToInstallments(student, amount);
        return saved;
    }

    /** Record a payment without semester (backward compatibility) */
    @Transactional
    public PaymentTransaction record(User student, User cashier, BigDecimal amount, String method, String reference, String note) {
        return record(student, cashier, amount, null, method, reference, note);
    }

    /** Update a transaction amount and adjust both balance and installment allocations */
    @Transactional
    public PaymentTransaction updateAmount(Long txId, BigDecimal newAmount) {
        PaymentTransaction tx = txRepo.findById(txId).orElseThrow();
        BigDecimal old = tx.getAmount();
        if (newAmount.compareTo(old) == 0) return tx;

        tx.setAmount(newAmount);
        PaymentTransaction saved = txRepo.save(tx);

        // Adjust balance
        User student = tx.getStudent();
        BigDecimal delta = newAmount.subtract(old);
        if (delta.compareTo(BigDecimal.ZERO) > 0) {
            balanceService.addToPaid(student, delta);
            balanceService.applyPaymentToInstallments(student, delta);
        } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
            // Rollback: decrease amountPaid and regenerate allocations by recomputing schedule payments.
            balanceService.subtractFromPaid(student, delta.negate());
            // Simplest approach: clear paid flags and re-apply all transactions in order.
            // (Keeps code short; good enough for this use case.)
            recomputeInstallmentApplications(student);
        }
        return saved;
    }

    /** Update a transaction including semester */
    @Transactional
    public PaymentTransaction updateTransaction(Long txId, BigDecimal newAmount, String semester) {
        PaymentTransaction tx = txRepo.findById(txId).orElseThrow();
        BigDecimal old = tx.getAmount();

        tx.setAmount(newAmount);
        tx.setSemester(semester);
        PaymentTransaction saved = txRepo.save(tx);

        // Adjust balance if amount changed
        if (newAmount.compareTo(old) != 0) {
            User student = tx.getStudent();
            BigDecimal delta = newAmount.subtract(old);
            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                balanceService.addToPaid(student, delta);
                balanceService.applyPaymentToInstallments(student, delta);
            } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
                balanceService.subtractFromPaid(student, delta.negate());
                recomputeInstallmentApplications(student);
            }
        }
        return saved;
    }

    /** Delete a transaction and roll back its effects */
    @Transactional
    public void delete(Long txId) {
        PaymentTransaction tx = txRepo.findById(txId).orElseThrow();
        User student = tx.getStudent();
        BigDecimal amount = tx.getAmount();
        txRepo.delete(tx);

        // Roll back balance and recompute installation allocations from scratch
        balanceService.subtractFromPaid(student, amount);
        recomputeInstallmentApplications(student);
    }

    /** Re-applies all existing transactions to installments in chronological order */
    private void recomputeInstallmentApplications(User student) {
        // Reset installments
        balanceService.getSchedule(student).forEach(pi -> {
            pi.setAmountPaid(BigDecimal.ZERO);
            pi.setPaid(false);
            pi.setPaidAt(null);
        });

        // Re-apply payments oldest to newest
        List<PaymentTransaction> all = txRepo.findByStudentOrderByRecordedAtDesc(student);
        all.stream()
                .sorted((a,b) -> a.getRecordedAt().compareTo(b.getRecordedAt()))
                .forEach(t -> balanceService.applyPaymentToInstallments(student, t.getAmount()));
    }

    /** Find transactions by student and semester */
    @Transactional(readOnly = true)
    public List<PaymentTransaction> findByStudentAndSemester(User student, String semester) {
        return txRepo.findByStudentAndSemesterOrderByRecordedAtDesc(student, semester);
    }

    /** Get total payments by semester for a student */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaymentsBySemester(User student, String semester) {
        List<PaymentTransaction> transactions = txRepo.findByStudentAndSemester(student, semester);
        return transactions.stream()
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}