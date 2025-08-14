package com.example.studentportal.repository;

import com.example.studentportal.model.PaymentTransaction;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByStudentOrderByRecordedAtDesc(User student);
}
