package com.gianvittorio.libraryapi.libraryapi.serviceTest;

import com.gianvittorio.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.gianvittorio.libraryapi.libraryapi.exception.BusinessException;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import com.gianvittorio.libraryapi.libraryapi.model.repository.LoanRepository;
import com.gianvittorio.libraryapi.libraryapi.service.LoanService;
import com.gianvittorio.libraryapi.libraryapi.service.impl.LoanServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {
    @MockBean
    LoanRepository repository;

    LoanService service;

    @BeforeEach
    public void setUp() {
        service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Must save a loan.")
    public void saveLoanTest() {
        // Given
        String customer = "Fulano";
        Book book = Book.builder()
                .id(1l)
                .build();

        Loan savingLoan = newLoan();

        Loan savedLoan = Loan.builder()
                .id(1l)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        when(repository.save(savingLoan))
                .thenReturn(savedLoan);

        when(repository.existsByBookAndNotReturned(book))
                .thenReturn(false);

        // When
        Loan loan = service.save(savingLoan);

        // Then
        verify(repository)
                .save(savingLoan);

        assertThat(loan)
                .isNotNull();
        assertThat(loan.getId())
                .isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer())
                .isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate())
                .isEqualTo(savedLoan.getLoanDate());
        assertThat(loan.getBook().getId())
                .isEqualTo(savedLoan.getBook().getId());
    }

    @Test
    @DisplayName("Must throw business error exception on trying to loan an unavailable book.")
    public void loanedBookSaveTest() {
        // Given
        String customer = "Fulano";
        Book book = Book.builder()
                .id(1l)
                .build();

        Loan savingLoan = Loan.builder()
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        when(repository.existsByBookAndNotReturned(book))
                .thenReturn(true);

        // When
        Throwable runtimeException = catchThrowable(() -> service.save(savingLoan));

        // Then
        assertThat(runtimeException)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book is currently loaned!");

        verify(repository, never())
                .save(savingLoan);
    }

    @Test
    @DisplayName("Must obtain loan details referred to by id.")
    public void getLoanDetailsTest() {
        // Given
        Long id = 1l;

        Loan loan = newLoan();
        loan.setId(id);

        when(repository.findById(id))
                .thenReturn(Optional.of(loan));

        // When
        Optional<Loan> foundLoan = service.getById(id);

        // Then
        assertThat(foundLoan.isPresent())
                .isTrue();
        assertThat(foundLoan.get().getId())
                .isEqualTo(id);
        assertThat(foundLoan.get().getCustomer())
                .isEqualTo(loan.getCustomer());
        assertThat(foundLoan.get().getLoanDate())
                .isEqualTo(loan.getLoanDate());
        assertThat(foundLoan.get().getBook().getId())
                .isEqualTo(loan.getBook().getId());

        verify(repository)
                .findById(id);
    }

    @Test
    @DisplayName("Must update a loan.")
    public void updateLoanTest() {
        // Given
        Long id = 1l;

        Loan updatedLoan = newLoan();
        updatedLoan.setId(id);
        updatedLoan.setReturned(true);

        when(repository.save(updatedLoan))
                .thenReturn(updatedLoan);

        // When
        Loan loan = service.update(updatedLoan);

        // Then
        verify(repository)
                .save(updatedLoan);

        assertThat(loan)
                .isNotNull();
        assertThat(loan.getReturned())
                .isTrue();
    }

    @SneakyThrows
    @Test
    @DisplayName("Must filter loans by properties.")
    public void findLoansTest() {
        // Given
        Long id = 1l;
        String customer = "Fulano";
        String isbn = "321";

        Loan loan = newLoan();
        loan.setId(id);
        loan.setCustomer(customer);
        Book book = Book.builder().id(id).isbn(isbn).build();
        loan.setBook(book);

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder()
                .isbn(isbn)
                .customer(customer)
                .build();

        int pageNumber = 0, pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Mockito.when(
                repository.findByBookIsbnOrCustomer(
                        anyString(),
                        anyString(),
                        any(PageRequest.class)
                )
        )
                .thenReturn(
                        new PageImpl<>(
                                Arrays.asList(loan),
                                PageRequest.of(0, 10),
                                1)
                );

        // When
        Page<Loan> loans = service.find(loanFilterDTO, pageRequest);

        // Then
        assertThat(loans)
                .isNotNull();

        assertThat(loans.getContent())
                .hasSize(1);

        assertThat(loans.getContent().get(0))
                .isEqualTo(loan);

        verify(repository)
                .findByBookIsbnOrCustomer(anyString(), anyString(), any(PageRequest.class));
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

        when(repository.findByLoanDateLessThanAndNotReturned(loanDate))
                .thenReturn(Collections.singletonList(loan));

        // When
        List<Loan> lateLoans = service.getAllLateLoans();

        // Then
        verify(repository)
                .findByLoanDateLessThanAndNotReturned(loanDate);

        assertThat(lateLoans)
                .isNotNull();
        assertThat(lateLoans)
                .hasSize(1)
                .contains(loan);
    }

    @Test
    @DisplayName("Must return empty list whenever there are no late loans.")
    public void findNotByLoanDateLessThanAndNotReturnedTest() {
        // Given
        when(repository.findByLoanDateLessThanAndNotReturned(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        // When
        List<Loan> allLateLoans = service.getAllLateLoans();

        // Then
        assertThat(allLateLoans)
                .isEmpty();
    }

    public static Loan newLoan() {
        Book book = Book.builder()
                .id(1l)
                .build();

        String customer = "Fulano";

        return Loan.builder()
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();
    }
}
