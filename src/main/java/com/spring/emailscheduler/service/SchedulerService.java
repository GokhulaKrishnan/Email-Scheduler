package com.spring.emailscheduler.service;

import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.BillReminder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * This class is a scheduler which runs every 30 minutes and fetches the reminder bills and sends the
 * email. Then mark it as sent.
 */
@Service
public class SchedulerService {

    @Autowired
    private BillReminderService billReminderService;

    @Autowired
    private EmailService emailService;

    // Here we are going to create a scheduler which runs every 12 hours once
    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    @Transactional
    public void sendPendingReminders() {

        try{
            System.out.println("Checking for pending reminders");

            // Fetching all the pending reminders
            List<BillReminder> billReminders = billReminderService.getPendingReminders();

            // If there are no pending reminders
            if(billReminders.isEmpty()){
                System.out.println("No pending reminders found");
                return;
            }

            System.out.println("Found " + billReminders.size() + " pending reminders");

            // For every pending reminder, we are going to send an email
            for (BillReminder billReminder : billReminders) {
                try{
                    // Getting the Bill Object
                    Bill bill = billReminder.getBill();
                    System.out.println("📧 [SCHEDULER] Sending reminder for bill: " + bill.getName());

                    // Sending the email
                    emailService.sendBillReminderEmail(billReminder);

                    // Marking the email as sent
                    billReminderService.markReminderAsSent(billReminder.getId(), "Bill Reminder: " +
                            billReminder.getBill().getName(), "Reminder Sent successfully");

                    System.out.println("Reminder sent for bill: " + billReminder.getBill().getName());

                }catch (Exception e){
                    System.out.println("Error sending reminder: " + billReminder.getBill().getName()
                            + " - " + e.getMessage());
                }
            }


        }catch (Exception e){
            System.out.println("Error sending reminders: " + e.getMessage());
        }
    }
}
