package com.online.gallery.mail.impl;

import com.online.gallery.mail.MailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSenderImpl implements MailSender {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mail;

    public MailSenderImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendConfirmationEmail(String text, String toAddress, String subject) throws MessagingException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setText(text, true);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setFrom(mail);
            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            throw new MessagingException("Failed when trying to send the confirmation email.");
        }
    }
}
