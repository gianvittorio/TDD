package com.gianvittorio.libraryapi.libraryapi.repositoryTest;

import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class BookRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Must return true whenever informed ISBN exists.")
    public void returnTrueWhenISBNExists() {
        // Scenario
        String isbn = "123";
        Book book = newBook(isbn);

        entityManager.persist(book);

        // Execution
        boolean exists = repository.existsByIsbn(isbn);

        // Verification
        assertThat(exists)
                .isTrue();
    }

    public static Book newBook(String isbn) {
        return Book.builder()
                .title("Aventuras")
                .author("Fulano")
                .isbn(isbn)
                .build();
    }

    @Test
    @DisplayName("Must return false whenever informed ISBN does not exist.")
    public void returnFalseWhenISBNDoesNotExist() {
        // Scenario
        String isbn = "123";

        // Execution
        boolean exists = repository.existsByIsbn(isbn);

        // Verification
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Must find book by id.")
    public void findByIdTest() {
        // Scenario
        Book book = newBook("123");
        entityManager.persist(book);

        // Execution
        Optional<Book> foundBook = repository.findById(book.getId());

        // Verification
        assertThat(foundBook.isPresent())
                .isTrue();
    }

    @Test
    @DisplayName("Must delete book referred to by id")
    public void deleteBookTest() {
        // Given
        Book book = newBook("123");
        entityManager.persist(book);

        // When
        Book foundBook = entityManager.find(Book.class, book.getId());

        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());


        // Then
        assertThat(deletedBook)
                .isNull();
    }

    @Test
    @DisplayName("Must save book.")
    public void saveBookTest() {
        // Given
        Book book = newBook("123");

        // When
        long id = (long) entityManager.persistAndGetId(book);
        Optional<Book> foundBook = repository.findById(id);

        // Then
        assertThat(repository.existsById(id))
                .isTrue();
    }
}
