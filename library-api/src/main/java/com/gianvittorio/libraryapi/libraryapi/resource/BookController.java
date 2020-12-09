package com.gianvittorio.libraryapi.libraryapi.resource;

import com.gianvittorio.libraryapi.libraryapi.dto.BookDTO;
import com.gianvittorio.libraryapi.libraryapi.dto.LoanDTO;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Loan;
import com.gianvittorio.libraryapi.libraryapi.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
@Api("Books API")
public class BookController {
    private final BookService service;
    private final ModelMapper modelMapper;

    @PostMapping
    @ApiOperation("Create a book")
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto) {
        Book entity = service.save(modelMapper.map(dto, Book.class));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(entity, BookDTO.class));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ApiOperation("Obtains book details referred to by id")
    public ResponseEntity<BookDTO> get(@PathVariable Long id) {
        BookDTO book = service.getById(id)
                .map(b -> modelMapper.map(b, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(modelMapper.map(book, BookDTO.class));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Find books by params")
    public Page<BookDTO> find(
            BookDTO dto,
            Pageable pageRequest
    ) {
        Book book = modelMapper.map(dto, Book.class);

        Page<Book> res = service.find(book, pageRequest);

        List<BookDTO> bookDTOs = res.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(bookDTOs, pageRequest, res.getTotalElements());

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes book referred to by id")
    @ApiResponses(
            {
                    @ApiResponse(code = 204, message = "Book successfully deleted")
            }
    )
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("Updates book referred to by id")
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        BookDTO responseDTO = service.getById(id)
                .map(b -> {
                    b.setAuthor(bookDTO.getAuthor());
                    b.setTitle(bookDTO.getTitle());

                    b = service.update(b);

                    return modelMapper.map(b, BookDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}/loans")
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Page<Loan> loans = service.getLoansByBook(book, pageable);

        List<LoanDTO> loanDTOS = loans.getContent()
                .stream()
                .map(loan -> {
                            BookDTO bookDTO = modelMapper.map(loan.getBook(), BookDTO.class);
                            LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);

                            loanDTO.setBook(bookDTO);

                            return loanDTO;
                        }
                )
                .collect(Collectors.toList());

        return new PageImpl<>(loanDTOS, pageable, loans.getTotalElements());
    }
}
