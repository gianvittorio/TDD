package com.gianvittorio.libraryapi.libraryapi.repositoryTest;

import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import com.gianvittorio.libraryapi.libraryapi.model.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.gianvittorio.libraryapi.libraryapi.repositoryTest.BookRepositoryTest.newBook;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Must check if the book referred to by the loan has been returned already.")
    public void existsByBookAndNotReturnedTest() {
        // Given
        String isbn = "321";
        String customer = "Fulano";

        Book book = newBook(isbn);
        entityManager.persist(book);

        Loan loan = Loan.builder()
                .book(book)
                .loanDate(LocalDate.now())
                .customer(customer)
                .build();
        entityManager.persist(loan);

        // When
        boolean result = repository.existsByBookAndNotReturned(book);

        // Then
        assertThat(result)
                .isTrue();
    }

    @Test
    @DisplayName("Must search for loan by isbn or customer.")
    public void findByBookIsbnOrCustomerTest() {
        // Giver
        String isbn = "321";
        String customer = "Fulano";

        Book book = Book.builder()
                .isbn(isbn)
                .build();
        entityManager.persist(book);

        Loan loan = Loan.builder()
                .customer(customer)
                .book(book)
                .build();
        entityManager.persist(loan);

        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);

        // When
        Page<Loan> loans = repository.findByBookIsbnOrCustomer(isbn, customer, pageRequest);

        // Then
        assertThat(loans)
                .isNotNull();
        assertThat(loans.getContent())
                .hasSize(1);
        assertThat(loans.getContent())
                .contains(loan);
        assertThat(loans.getTotalElements())
                .isEqualTo(1);
        assertThat(loans.getPageable().getPageNumber())
                .isEqualTo(pageNumber);
        assertThat(loans.getPageable().getPageSize())
                .isEqualTo(pageSize);

    }

    @Test
    @DisplayName("Must return loans older than given date, which have not been returned.")
    public void findByLoanDateLessThanAndNotReturnedTest() {
        // Given
        String customer = "fulano";
        String customerEmail = customer.concat("@domain.com");
        LocalDate today = LocalDate.now();
        LocalDate loanDate = today.minusDays(4);

        Loan loan = Loan.builder()
                .customer(customer)
                .customerEmail(customerEmail)
                .loanDate(loanDate)
                .build();

        entityManager.persist(loan);

        // When
        List<Loan> lateLoans = repository.findByLoanDateLessThanAndNotReturned(today);

        // Then
        assertThat(lateLoans)
                .isNotNull();
        assertThat(lateLoans)
                .hasSize(1)
                .contains(loan);
    }
}
