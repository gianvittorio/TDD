package com.gianvittorio.libraryapi.libraryapi;

import com.gianvittorio.libraryapi.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class Application {
    @Autowired
    EmailService emailService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            emailService.sendMail(
                    List.of("c36a3f12b5-fe3fcf@inbox.mailtrap.io"),
                    "Testing email service..."
            );

            System.out.println("Email sent.");
        };
    }

    @Bean
    ModelMapper createModelMapper() {
        return new ModelMapper();
    }
}
