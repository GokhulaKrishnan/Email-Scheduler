package com.spring.emailscheduler.controller;

import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.BillReminder;
import com.spring.emailscheduler.service.BillService;
import com.spring.emailscheduler.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private BillService billService;

    @PostMapping("/send-email/{billId}")
    public ResponseEntity<String> testEmailForBill(@PathVariable Long billId) {
        try {
            Bill bill = billService.getBillById(billId);

            // Create a test reminder
            BillReminder testReminder = new BillReminder(
                    LocalDateTime.now(),
                    3,
                    bill
            );

            emailService.sendBillReminderEmail(testReminder);

            return ResponseEntity.ok("Test email sent successfully to: " + bill.getUser().getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/send-test-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String email) {
        try {
            emailService.sendTestEmail(email);
            return ResponseEntity.ok("Test email sent to: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }
}