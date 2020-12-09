package com.gianvittorio.libraryapi.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendMail(List<String> mailList, String message);
}
