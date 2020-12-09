package com.gianvittorio.libraryapi.libraryapi.model.repository;

import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

//    Page<Loan> findByBook(Book , org.springframework.data.domain.Pageable );
}
