package com.gianvittorio.libraryapi.libraryapi.service;

import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduledService {
    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    private final String message;

    private final LoanService loanService;
    private final EmailService emailService;

    ScheduledService(
            LoanService loanService,
            EmailService emailService,
            @Value("${application.mail.lateloans.message}") String message) {
        this.loanService = loanService;
        this.emailService = emailService;
        this.message = message;
    }

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendEmailToLateLoaners() {
        List<Loan> lateLoans = loanService.getAllLateLoans();
        List<String> mailList = lateLoans.stream()
                .map(Loan::getCustomerEmail)
                .collect(Collectors.toList());

        emailService.sendMail(mailList, message);
    }
}
