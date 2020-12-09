package com.gianvittorio.libraryapi.libraryapi.service;

import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book) throws IllegalArgumentException;

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);

    Optional<Book> getBookByIsbn(String isbn);

    Page<Loan> getLoansByBook(Book book, Pageable pageable);
}
