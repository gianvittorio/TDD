package com.gianvittorio.libraryapi.libraryapi.serviceTest;

import com.gianvittorio.libraryapi.libraryapi.exception.BusinessException;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.repository.BookRepository;
import com.gianvittorio.libraryapi.libraryapi.service.BookService;
import com.gianvittorio.libraryapi.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookServiceTest {
    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Must save book.")
    public void saveBookTest() {
        // Scenario
        Book book = newValidBook();

        when(repository.existsByIsbn(anyString()))
                .thenReturn(false);
        when(repository.save(book))
                .thenReturn(
                        Book.builder()
                                .id(1l)
                                .isbn("123")
                                .title("The Adventures")
                                .author("John Doe")
                                .build()
                );

        // Execution
        Book savedBook = service.save(book);

        // Verification
        assertThat(savedBook.getId())
                .isNotNull();

        assertThat(savedBook.getIsbn())
                .isEqualTo(book.getIsbn());

        assertThat(savedBook.getTitle())
                .isEqualTo(book.getTitle());

        assertThat(savedBook.getAuthor())
                .isEqualTo(book.getAuthor());

    }

    @Test
    @DisplayName("Must throw business error exception whenever trying to insert book with duplicated ISBN.")
    public void mustNotSaveBookWithDuplicatedISBN() {
        // Scenario
        Book book = newValidBook();

        when(repository.existsByIsbn(anyString()))
                .thenReturn(true);

        // Execution
        Throwable exception = catchThrowable(() -> service.save(book));

        // Verification
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("ISBN already exists!");

        verify(repository, never())
                .save(book);

    }

    @Test
    @DisplayName("Must get book by id")
    public void getByIdTest() {
        // Given
        Long id = 1l;
        Book book = newValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(book));

        // When
        Optional<Book> foundBook = service.getById(id);


        // Then
        assertThat(foundBook.isPresent())
                .isTrue();
        assertThat(foundBook.get().getId())
                .isEqualTo(id);
        assertThat(foundBook.get().getAuthor())
                .isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle())
                .isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn())
                .isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Must return empty whenever book referred to by id does not exist.")
    public void bookNotFoundByIdTest() {
        // Given
        Long id = 1l;

        Mockito.when(repository.findById(id))
                .thenReturn(Optional.empty());

        // When
        Optional<Book> foundBook = service.getById(id);

        // Then
        assertThat(foundBook.isEmpty())
                .isTrue();
    }

    @Test
    @DisplayName("Must delete book referred by id whenever found.")
    public void deleteBookTest() throws IllegalAccessException {
        // Given
        Book book = newValidBook();
        book.setId(1l);

        // When
        // Then
        Assertions.assertDoesNotThrow(() -> service.delete(book));

        verify(repository)
                .delete(book);
    }

    @Test
    @DisplayName("Must throw illegal argument exception whenever book is null or its id is null.")
    public void deleteInvalidBookTest() {
        // Given
        Book book = null;

        // When
        // Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        verify(repository, never())
                .delete(any(Book.class));
    }

    @Test
    @DisplayName("Must update book referred by id whenever found.")
    public void updateBookTest() {
        // Given
        long id = 1l;
        Book updatingBook = Book.builder()
                .id(id)
                .build();

        // Simulation
        Book updatedBook = newValidBook();
        updatedBook.setId(id);

        when(repository.save(updatingBook))
                .thenReturn(updatedBook);

        // When
        Book book = service.update(updatingBook);

        // Then
        assertThat(book.getId())
                .isEqualTo(updatedBook.getId());
        assertThat(book.getTitle())
                .isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor())
                .isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn())
                .isEqualTo(updatedBook.getIsbn());

        verify(repository)
                .save(updatingBook);
    }

    @Test
    @DisplayName("Must throw illegal argument runtime exception, whenever book is null or id is null")
    public void updateInvalidBookTest() {
        // Given
        Book book = null;

        // When
        // Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        verify(repository, never())
                .save(any(Book.class));
    }

    @Test
    @DisplayName("Must filter books.")
    public void findBookTest() {
        // Given
        Book book = newValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> page = new PageImpl<>(books, pageRequest, 1);

        Mockito.when(repository.findAll(any(Example.class), any(PageRequest.class)))
                .thenReturn(page);

        // When
        Page<Book> result = service.find(book, pageRequest);


        // Then
        assertThat(result.getTotalElements())
                .isEqualTo(1);
        assertThat(result.getContent())
                .isEqualTo(books);
        assertThat(result.getPageable().getPageNumber())
                .isEqualTo(0);
        assertThat(result.getPageable().getPageSize())
                .isEqualTo(10);

    }

    @Test
    @DisplayName("Must get book referred to by Isbn.")
    public void getBookByIsbn() {
        // Given
        Book book = newValidBook();
        book.setId(1l);

        Mockito.when(repository.findByIsbn(book.getIsbn()))
                .thenReturn(Optional.of(book));

        // When
        Optional<Book> foundBook = service.getBookByIsbn(book.getIsbn());

        // Then
        verify(repository).findByIsbn(book.getIsbn());

        assertThat(foundBook.isPresent())
                .isTrue();

        assertThat(foundBook.get().getId())
                .isEqualTo(book.getId());

        assertThat(foundBook.get().getIsbn())
                .isEqualTo(book.getIsbn());
    }

    private static Book newValidBook() {
        return Book.builder()
                .isbn("123")
                .author("John Doe")
                .title("The Adventures")
                .build();
    }
}
