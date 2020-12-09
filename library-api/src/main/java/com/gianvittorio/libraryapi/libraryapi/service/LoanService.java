package com.gianvittorio.libraryapi.libraryapi.service;

import com.gianvittorio.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO dto, Pageable pageRequest);

    List<Loan> getAllLateLoans();
}
