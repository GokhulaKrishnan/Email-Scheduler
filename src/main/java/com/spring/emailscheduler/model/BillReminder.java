package com.spring.emailscheduler.model;

// This class is used to represent a reminder entity.
// It contains all the details that should be needed to send it to the client to remind about the bill.

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "bill_reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BillReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reminder_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderDate;

    @Column(name = "days_before")
    private int daysBefore;

    @Column(name = "sent")
    private boolean sent = false;

    @Column(name = "sent_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;

    @Column(name = "email_subject")
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // It should contain the bill for which the reminder has to be sent
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonIgnore
    private Bill bill;

    // Modified Constructor
    public BillReminder(LocalDateTime reminderDate, int daysBefore, Bill bill) {
        this.reminderDate = reminderDate;
        this.daysBefore = daysBefore;
        this.bill = bill;
        this.createdAt = LocalDateTime.now();
    }

    // This will run and set the createdAt automatically when we create a reminder.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
