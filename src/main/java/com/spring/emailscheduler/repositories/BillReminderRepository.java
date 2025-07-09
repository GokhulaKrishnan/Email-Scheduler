package com.spring.emailscheduler.repositories;

import com.spring.emailscheduler.model.BillReminder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillReminderRepository extends JpaRepository<BillReminder, Long> {

    // Getting all the pending reminders.
    @Query("SELECT r FROM BillReminder r WHERE r.reminderDate <= ?1 AND r.sent = false")
    List<BillReminder> findPendingReminders(LocalDateTime now);


    List<BillReminder> findByBillId(Long billId);

    @Transactional
    @Modifying
    @Query("DELETE FROM BillReminder r WHERE r.bill.id = ?1")
    void deleteByBillId(Long id);
}
