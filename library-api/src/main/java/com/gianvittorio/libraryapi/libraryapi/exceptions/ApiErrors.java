package com.gianvittorio.libraryapi.libraryapi.exceptions;

import com.gianvittorio.libraryapi.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ApiErrors {
    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        errors = bindingResult.getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
    }

    public ApiErrors(BusinessException e) {
        errors = Arrays.asList(e.getMessage());
    }

    public ApiErrors(ResponseStatusException e) {
        errors = Arrays.asList(e.getReason());
    }

    public List<String> getErrors() {
        return errors;
    }
}
