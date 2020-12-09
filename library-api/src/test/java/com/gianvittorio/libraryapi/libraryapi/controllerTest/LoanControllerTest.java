package com.gianvittorio.libraryapi.libraryapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gianvittorio.libraryapi.libraryapi.dto.LoanDTO;
import com.gianvittorio.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.gianvittorio.libraryapi.libraryapi.dto.ReturnedLoanDTO;
import com.gianvittorio.libraryapi.libraryapi.exception.BusinessException;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import com.gianvittorio.libraryapi.libraryapi.resource.LoanController;
import com.gianvittorio.libraryapi.libraryapi.service.BookService;
import com.gianvittorio.libraryapi.libraryapi.service.LoanService;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static com.gianvittorio.libraryapi.libraryapi.serviceTest.LoanServiceTest.newLoan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {
    private static String LOAN_API = "/api/v1/loan";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Must create loan.")
    public void createLoanTest() throws Exception {
        // Given
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Fulano")
                .email("customer@email.com")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder()
                .isbn("123")
                .id(1l)
                .build();
        given(bookService.getBookByIsbn(dto.getIsbn()))
                .willReturn(Optional.of(book));

        Loan loan = Loan.builder()
                .id(1l)
                .customer("Fulano")
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        given(loanService.save(any(Loan.class)))
                .willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // When
        // Then
        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Must throw on trying to loan a non existing book.")
    public void invalidIsbnLoanTest() throws Exception {
        // Given
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getBookByIsbn(dto.getIsbn()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // When
        // Then
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for provided Isbn!"));
    }

    @Test
    @DisplayName("Must throw on trying to loan an unavailable book.")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {
        // Given
        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("Fulano")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder()
                .isbn("123")
                .id(1l)
                .build();
        given(bookService.getBookByIsbn(dto.getIsbn()))
                .willReturn(Optional.of(book));

        given(loanService.save(any(Loan.class)))
                .willThrow(new BusinessException("Book is currently loaned!"));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // When
        // Then
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book is currently loaned!"));
    }

    @Test
    @DisplayName("Must return a book.")
    public void returnBookTest() throws Exception {
        // Given
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder()
                .returned(true)
                .build();

        Loan loan = Loan.builder().id(1l).build();
        given(loanService.getById(anyLong()))
                .willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // When
        // Then
        mvc.perform(request)
                .andExpect(status().isOk());

        verify(loanService)
                .getById(anyLong());
    }

    @Test
    @DisplayName("Must return 404 on trying to hand over non existing book")
    public void returnNonExistingBookTest() throws Exception {
        // Given
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder()
                .returned(true)
                .build();

        given(loanService.getById(anyLong()))
                .willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.patch(LOAN_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // When
        // Then
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    @DisplayName("Must filter loans.")
    public void findLoansTest() {
        // Given
        Long id = 1l;

        Loan loan = newLoan();
        loan.setId(id);
        Book book = Book.builder().id(1l).isbn("321").build();
        loan.setBook(book);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(
                        new PageImpl<>(Arrays.asList(loan), PageRequest.of(0, 10), 1)
                );

        String queryString = String.format(
                "?isbn=%s&customer=%s&page=0&size=10",
                book.getIsbn(),
                loan.getCustomer()
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }
}
