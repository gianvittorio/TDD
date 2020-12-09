package com.gianvittorio.libraryapi.libraryapi.service.impl;

import com.gianvittorio.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.gianvittorio.libraryapi.libraryapi.exception.BusinessException;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import com.gianvittorio.libraryapi.libraryapi.model.repository.LoanRepository;
import com.gianvittorio.libraryapi.libraryapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {
    LoanRepository repository;

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book is currently loaned!");
        }

        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO dto, Pageable pageRequest) {
        return repository.findByBookIsbnOrCustomer(dto.getIsbn(), dto.getCustomer(), pageRequest);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(4);

        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }

    @Autowired
    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }
}
