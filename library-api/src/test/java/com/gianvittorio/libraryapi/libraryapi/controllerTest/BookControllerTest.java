package com.gianvittorio.libraryapi.libraryapi.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gianvittorio.libraryapi.libraryapi.dto.BookDTO;
import com.gianvittorio.libraryapi.libraryapi.exception.BusinessException;
import com.gianvittorio.libraryapi.libraryapi.model.entity.Book;
import com.gianvittorio.libraryapi.libraryapi.service.BookService;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {
    static String BOOK_API = "/api/v1/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Must successfully create a book.")
    public void createBookTest() throws Exception {
        BookDTO dto = BookDTO.builder()
                .author("Artur")
                .title("As Aventuras")
                .isbn("001")
                .build();

        Book savedBook = Book.builder().author("Artur")
                .id(1l)
                .title("As Aventuras")
                .isbn("001")
                .build();

        BDDMockito.given(service.save(any(Book.class)))
                .willReturn(savedBook);

        String json = new ObjectMapper()
                .writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(
                        status()
                                .isCreated()
                )
                .andExpect(
                        jsonPath("id")
                                .isNotEmpty()
                )
                .andExpect(jsonPath("title").value(dto.getTitle()))
                .andExpect(jsonPath("author").value(dto.getAuthor()))
                .andExpect(jsonPath("isbn").value(dto.getIsbn()));

    }

    @Test
    @DisplayName("Must throw error on trying to create book without sufficient infos.")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper()
                .writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Must throw whenever trying to create book with duplicated ISBN.")
    public void createBookWithDuplicatedIsbn() throws Exception {
        Book dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);

        final String ERROR_MESSAGE = "ISBN already exists!";

        BDDMockito.given(service.save(any(Book.class)))
                .willThrow(new BusinessException(ERROR_MESSAGE));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Must fetch details on book.")
    public void getBookDetailsTest() throws Exception {

        // Given
        Long id = 1l;

        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(book));

        // When
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // Then
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));

    }

    @Test
    @DisplayName("Must throw ResourceNotFound exception when book is not found.")
    public void bookNotFoundTest() throws Exception {
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.empty());


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Must delete book referred by id.")
    public void deleteBookTest() throws Exception {
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(
                        Optional.of(
                                Book.builder()
                                        .id(1l)
                                        .build()
                        )
                );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1));

        mvc.perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Must return resource not found code whenever book is not found.")
    public void deleteNonExistingBookTest() throws Exception {
        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1));

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Must update book when id is found.")
    public void updateBookTest() throws Exception {
        Long id = 1l;
        String json = new ObjectMapper()
                .writeValueAsString(createNewBook());

        Book updatingBook = Book.builder()
                .id(1l)
                .title("Any Title")
                .author("Any Author")
                .isbn("321")
                .build();

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder()
                .id(1l)
                .author("Artur")
                .title("As Aventuras")
                .isbn("321")
                .build();

        BDDMockito.given(service.update(updatingBook))
                .willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));
    }

    @Test
    @DisplayName("Must return 404 whenever trying to update non existing book.")
    public void updateNonExistingBookTest() throws Exception {
        String json = new ObjectMapper()
                .writeValueAsString(createNewBook());

        BDDMockito.given(service.getById(anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1l))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    @DisplayName("Must filter books.")
    public void findBooksTest() {
        Long id = 1l;
        Book book = Book.builder()
                .id(id)
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .isbn(createNewBook().getIsbn())
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(
                        new PageImpl<Book>(List.of(book), PageRequest.of(0, 100), 1)
                );

        String queryString = String.format(
                "?title=%s&author=%s&page=0&size=100",
                book.getTitle(),
                book.getAuthor()
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }

    private static Book createNewBook() {
        return Book.builder()
                .author("Artur")
                .title("As Aventuras")
                .isbn("001")
                .build();
    }
}
