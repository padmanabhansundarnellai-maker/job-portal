package com.portfolio.jobportal.service;

import com.portfolio.jobportal.model.AppStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final ObjectProvider<JavaMailSender> senderProvider;
    private final String from;

    public EmailService(ObjectProvider<JavaMailSender> senderProvider,
                        @Value("${app.mail.from}") String from) {
        this.senderProvider = senderProvider;
        this.from = from;
    }

    public void sendStatusUpdate(String to, String jobTitle, AppStatus status) {
        String subject = "Your application for " + jobTitle + " is now " + status;
        String body = "Hello,\n\nThe status of your application for \"" + jobTitle + "\" changed to " + status + ".\n\nJob Portal";
        JavaMailSender sender = senderProvider.getIfAvailable();
        if (sender == null) {
            log.info("[email disabled] To: {} | {}", to, subject);
            return;
        }
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(from);
            m.setTo(to);
            m.setSubject(subject);
            m.setText(body);
            sender.send(m);
        } catch (Exception e) {
            log.warn("Could not send email to {}: {}", to, e.getMessage());
        }
    }
}
