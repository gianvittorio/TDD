package com.gianvittorio.libraryapi.libraryapi.resource;

import com.gianvittorio.libraryapi.libraryapi.dto.BookDTO;
import com.gianvittorio.libraryapi.libraryapi.dto.LoanDTO;
import com.gianvittorio.libraryapi.libraryapi.dto.LoanFilterDTO;
import com.gianvittorio.libraryapi.libraryapi.dto.ReturnedLoanDTO;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import com.gianvittorio.libraryapi.libraryapi.service.BookService;
import com.gianvittorio.libraryapi.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/loan")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Book not found for provided Isbn!"
                        )
                );

        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = loanService.save(entity);

        return entity.getId();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = loanService.getById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("Book referred to by %d does not exist!", id)
                        )
                );

        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> find(LoanFilterDTO filter, Pageable pageRequest) {
        Page<Loan> page = loanService.find(filter, pageRequest);

        List<LoanDTO> loanDTOs = page.getContent()
                .stream()
                .map(
                        loan -> {
                            BookDTO bookDTO = modelMapper.map(loan.getBook(), BookDTO.class);
                            LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);

                            loanDTO.setBook(bookDTO);

                            return loanDTO;
                        }
                )
                .collect(Collectors.toList());

        return new PageImpl<>(loanDTOs, pageRequest, page.getTotalElements());
    }
}
