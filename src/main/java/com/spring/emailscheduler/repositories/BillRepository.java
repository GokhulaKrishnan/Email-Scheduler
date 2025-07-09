package com.spring.emailscheduler.repositories;

import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByUserIdAndIsActiveTrue(Long userId);

    @Query("SELECT b FROM Bill b WHERE b.user.id = ?1 AND b.dueDate BETWEEN ?2 AND ?3 AND b.isActive = true")
    List<Bill> findBillsInDateRange(Long userId, LocalDate start, LocalDate end);

    // Here we are grabbing all the bills which has before due date when compared to the current date
    @Query("SELECT b FROM Bill b WHERE b.dueDate <= ?1 AND b.isActive = true")
    List<Bill> findOverDueBills(LocalDate now);


    @Query("SELECT DISTINCT b.category FROM Bill b WHERE b.user.id = ?1")
    List<String> findCategoriesByUserId(Long userId);
}
