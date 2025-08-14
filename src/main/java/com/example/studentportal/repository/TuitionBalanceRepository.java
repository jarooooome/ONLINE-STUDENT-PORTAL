package com.example.studentportal.repository;

import com.example.studentportal.model.TuitionBalance;
import com.example.studentportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TuitionBalanceRepository extends JpaRepository<TuitionBalance, Long> {
    Optional<TuitionBalance> findByStudent(User student);
}
