package com.gianvittorio.libraryapi.libraryapi.model.repository;

import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
}
