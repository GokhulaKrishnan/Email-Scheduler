package com.spring.emailscheduler.service;

import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.BillReminder;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Used to Draft the email that needs to be sent to the user and will also send the drafted email.
 */
@Service
public class EmailService {

    // Using JavaMailSender to send the mail
    @Autowired
    private JavaMailSender mailSender;

    // Getting the values from the configuration
    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Method to send the mail
     * @param billReminder Object which contains information for reminder
     */
    public void sendBillReminderEmail(BillReminder billReminder) {

        // Checking if the email configuration is enabled
        if(!emailEnabled) {
            System.out.println("Email is disabled");
            return;
        }

        try{
            // Getting the Bill
            Bill bill = billReminder.getBill();
            // Creating a subject
            String subject = generateEmailSubject(bill, billReminder.getDaysBefore());
            // Creating a Body
            String messageBody  = generateEmailBody(bill, billReminder.getDaysBefore());

            // Object which is used to create raw email
            MimeMessage message = mailSender.createMimeMessage();
            // Wrapper to help with drafting the mail.
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            // Setting up the details in the helper
            messageHelper.setFrom(fromEmail);
            messageHelper.setSubject(subject);
            messageHelper.setText(messageBody, true);
            messageHelper.setTo(bill.getUser().getEmail());

            // Sending the mail
            mailSender.send(message);

            // Logging
            System.out.println("Email sent successfully to: " + bill.getUser().getEmail());

        } catch (Exception e){

            System.out.println("Email sending failed" + e.getMessage());
            throw new RuntimeException("Failed to send email reminder", e);
        }
    }

    /**
     * Used to test the email sending mechanism
     * @param toEmail Client email where the reminder to be sent
     */
    public void sendTestEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Test Email - Bill Payment System");

            String body = """
            <html>
            <body>
                <h2>Email Configuration Test</h2>
                <p>Email configuration is working correctly!</p>
                <p><strong>Timestamp:</strong> %s</p>
                <p><strong>From:</strong> Bill Payment Reminder System</p>
            </body>
            </html>
            """.formatted(LocalDateTime.now().toString());

            helper.setText(body, true);
            mailSender.send(message);

            System.out.println("Test email sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.err.println("Failed to send test email: " + e.getMessage());
            throw new RuntimeException("Failed to send test email", e);
        }
    }

    /**
     * Helper function to generate the subject.
     * @param bill Bill Object which contains the Bill information
     * @param daysBefore Days before the email to be sent before the due date
     * @return Formatted string which contains the due information
     */
    private String generateEmailSubject(Bill bill, int daysBefore) {
        if (daysBefore == 0) {
            return "Bill Due Today: " + bill.getName();
        } else if (daysBefore == 1) {
            return "Bill Due Tomorrow: " + bill.getName();
        } else {
            return "Bill Due in " + daysBefore + " days: " + bill.getName();
        }
    }


    /**
     * Helper function to generate the email body
     * @param bill Bill Object which contains the Bill information
     * @param daysBefore Days before the email to be sent before the due date
     * @return The body of the mail
     */
    private String generateEmailBody(Bill bill, int daysBefore) {
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String formattedDate = bill.getDueDate().format(formatDate);
        String formattedAmount = "$" + bill.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();

        // Building the Body in HTML
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>");
        body.append("<html><head><meta charset='UTF-8'></head><body>");
        body.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // Header
        body.append("<div style='background-color: #f8f9fa; padding: 20px; border-radius: 10px; margin-bottom: 20px;'>");
        body.append("<h2 style='color: #333; margin: 0;'>Bill Payment Reminder</h2>");
        body.append("</div>");

        // Main content
        body.append("<div style='background-color: white; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>");

        // Calculating the due time
        if (daysBefore == 0) {
            body.append("<div style='background-color: #ffebee; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>");
            body.append("<h3 style='color: #c62828; margin: 0;'>⚠️Bill Due Today!</h3>");
            body.append("</div>");
        } else if (daysBefore == 1) {
            body.append("<div style='background-color: #fff3e0; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>");
            body.append("<h3 style='color: #ef6c00; margin: 0;'>🔔Bill Due Tomorrow!</h3>");
            body.append("</div>");
        } else {
            body.append("<div style='background-color: #e8f5e8; padding: 15px; border-radius: 5px; margin-bottom: 20px;'>");
            body.append("<h3 style='color: #2e7d32; margin: 0;'>📅 Bill Due in " + daysBefore + " days</h3>");
            body.append("</div>");
        }

        body.append("<table style='width: 100%; border-collapse: collapse;'>");
        body.append("<tr><td style='padding: 10px 0; border-bottom: 1px solid #eee;'><strong>Bill Name:</strong></td>");
        body.append("<td style='padding: 10px 0; border-bottom: 1px solid #eee;'>" + bill.getName() + "</td></tr>");

        body.append("<tr><td style='padding: 10px 0; border-bottom: 1px solid #eee;'><strong>Amount:</strong></td>");
        body.append("<td style='padding: 10px 0; border-bottom: 1px solid #eee; color: #d32f2f; font-weight: bold;'>" + formattedAmount + "</td></tr>");

        body.append("<tr><td style='padding: 10px 0; border-bottom: 1px solid #eee;'><strong>Due Date:</strong></td>");
        body.append("<td style='padding: 10px 0; border-bottom: 1px solid #eee;'>" + formattedDate + "</td></tr>");

        body.append("<tr><td style='padding: 10px 0; border-bottom: 1px solid #eee;'><strong>Category:</strong></td>");
        body.append("<td style='padding: 10px 0; border-bottom: 1px solid #eee;'>" + bill.getCategory() + "</td></tr>");

        body.append("<tr><td style='padding: 10px 0; border-bottom: 1px solid #eee;'><strong>Frequency:</strong></td>");
        body.append("<td style='padding: 10px 0; border-bottom: 1px solid #eee;'>" + bill.getFrequency().getDisplayName() + "</td></tr>");

        if (bill.getDescription() != null && !bill.getDescription().trim().isEmpty()) {
            body.append("<tr><td style='padding: 10px 0;'><strong>Description:</strong></td>");
            body.append("<td style='padding: 10px 0;'>" + bill.getDescription() + "</td></tr>");
        }

        body.append("</table>");
        body.append("</div>");

        // Footer
        body.append("<div style='text-align: center; margin-top: 30px; color: #666; font-size: 14px;'>");
        body.append("<p>This is an automated reminder from the Bill Payment System.</p>");
        body.append("<p>Stay on top of your finances! 💪</p>");
        body.append("<p>Stop worrying about late day fees! 🔔</p>");
        body.append("</div>");

        body.append("</div>");
        body.append("</body></html>");

        return body.toString();
    }


}
