package com.gianvittorio.libraryapi.libraryapi.resource;

import com.gianvittorio.libraryapi.libraryapi.dto.BookDTO;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BookController {
    BookService service;
    ModelMapper modelMapper;

    @Autowired
    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto) {
        Book entity = service.save(modelMapper.map(dto, Book.class));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(entity, BookDTO.class));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<BookDTO> get(@PathVariable Long id) {
        BookDTO book = service.getById(id)
                .map(b -> modelMapper.map(b, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(modelMapper.map(book, BookDTO.class));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
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
    public void delete(@PathVariable Long id) throws IllegalAccessException {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(book);
    }

    @PutMapping("{id}")
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
}
