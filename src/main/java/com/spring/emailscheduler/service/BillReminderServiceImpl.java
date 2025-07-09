package com.spring.emailscheduler.service;

import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.BillReminder;
import com.spring.emailscheduler.repositories.BillReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class BillReminderServiceImpl implements BillReminderService {

    @Autowired
    private BillReminderRepository billReminderRepository;

    @Value("${app.reminder.days:7,3,1}")
    private String reminderDaysConfig;

    public List<BillReminder> getPendingReminders() {
        return billReminderRepository.findPendingReminders(LocalDateTime.now());
    }

    // Getting all the reminders for a bill Id
    public List<BillReminder> getRemindersByBillId(Long billId) {
        return billReminderRepository.findByBillId(billId);
    }

    // Creating a new bill reminder
    public void createRemindersForBill(Bill bill) {

        // To skip inactive bills
        if (!bill.isActive()) {
            return;
        }

        // Converting String into List of elements
        List<Integer> reminderDays = parseReminderDays();
        String userTimezone = bill.getUser().getTimeZone();

        // Here we are creating multiple remainders before 1, 3 and 7 days
        for (Integer daysBefore : reminderDays) {
            // Creating a new reminder for the particular daysBefore
            LocalDateTime reminderDateTime = calculateReminderDateTime(bill.getDueDate(), daysBefore, userTimezone);

            // Creating a new entry in the database
            BillReminder reminder = new BillReminder(reminderDateTime, daysBefore, bill);
            billReminderRepository.save(reminder);
        }
    }

    // When a bill is updated creating a new reminder
    public void updateRemindersForBill(Bill bill) {
        // Delete existing reminders
        List<BillReminder> existingReminders = billReminderRepository.findByBillId(bill.getId());
        billReminderRepository.deleteAll(existingReminders);

        // Create new reminders
        createRemindersForBill(bill);
    }


    // Here we are marking the bill as sent after we send the reminder
    public void markReminderAsSent(Long reminderId, String subject, String body) {
        BillReminder reminder = billReminderRepository.findById(reminderId)
                .orElseThrow(() -> new RuntimeException("Reminder not found with id: " + reminderId));

        reminder.setSent(true);
        reminder.setSentAt(LocalDateTime.now());
        reminder.setEmailSubject(subject);
        reminder.setEmailBody(body);

        billReminderRepository.save(reminder);
    }


    private List<Integer> parseReminderDays() {
        return Arrays.stream(reminderDaysConfig.split(",")).map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    private LocalDateTime calculateReminderDateTime(LocalDate dueDate, int daysBefore, String timezone) {

        // Here we are reducing the number of days from the due dates
        LocalDate reminderDate = dueDate.minusDays(daysBefore);

        // Here we are scheduling the reminder to be sent at 9:00 A.M
        ZonedDateTime zonedDateTime = reminderDate.atTime(9, 0).atZone(ZoneId.of(timezone));

        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}
