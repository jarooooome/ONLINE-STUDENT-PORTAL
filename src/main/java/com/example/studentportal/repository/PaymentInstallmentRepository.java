package com.example.studentportal.repository;

import com.example.studentportal.model.PaymentInstallment;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentInstallmentRepository extends JpaRepository<PaymentInstallment, Long> {
    List<PaymentInstallment> findByStudentOrderByDueDateAsc(User student);
    List<PaymentInstallment> findByStudentAndPaidFalseOrderByDueDateAsc(User student);
    PaymentInstallment findFirstByStudentAndPaidFalseOrderByDueDateAsc(User student);
    long countByStudentAndPaidFalse(User student);
    long countByStudentAndDueDateBeforeAndPaidFalse(User student, LocalDate date);
}
