package com.spring.emailscheduler.service;

import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.BillReminder;

import java.util.List;

public interface BillReminderService {

    List<BillReminder> getPendingReminders();

    List<BillReminder> getRemindersByBillId(Long billId);

    void updateRemindersForBill(Bill bill);

    void createRemindersForBill(Bill bill);

    void markReminderAsSent(Long reminderId, String subject, String body);
}
