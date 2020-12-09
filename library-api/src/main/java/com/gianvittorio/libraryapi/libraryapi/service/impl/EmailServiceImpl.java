package com.gianvittorio.libraryapi.libraryapi.service.impl;

import com.gianvittorio.libraryapi.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value(value = "${application.mail.default-sender}")
    private String sender;

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(List<String> mailList, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setSubject("Expired loan");
        mailMessage.setText(message);
        mailMessage.setTo(
                mailList.stream()
                        .toArray(String[]::new)
        );

        javaMailSender.send(mailMessage);
    }
}
