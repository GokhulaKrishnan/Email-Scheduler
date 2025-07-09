package com.spring.emailscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private int mailPort;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private String smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private String starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required:true}")
    private String starttlsRequired;


    /**
     * Custom JavaMailSender bean configuration.
     * Only creates this bean if app.email.enabled is true.
     * This prevents startup issues when email credentials are not provided.
     */
    @Bean
    @ConditionalOnProperty(name = "app.email.enabled", havingValue = "true", matchIfMissing = false)
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Basic SMTP configuration
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        // Advanced SMTP properties
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.starttls.required", starttlsRequired);


        // Additional security and connection settings
        props.put("mail.smtp.ssl.trust", mailHost);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // For Gmail specifically
        if (mailHost.contains("gmail")) {
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }

        return mailSender;
    }

    /**
     * Fallback JavaMailSender for when email is disabled.
     * This prevents autowiring issues in EmailService.
     */
    @Bean
    @ConditionalOnProperty(name = "app.email.enabled", havingValue = "false", matchIfMissing = true)
    public JavaMailSender dummyMailSender() {
        // Return a basic implementation that won't actually send emails
        return new JavaMailSenderImpl();
    }
}
