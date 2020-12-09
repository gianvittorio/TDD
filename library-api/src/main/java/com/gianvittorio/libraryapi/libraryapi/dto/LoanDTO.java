package com.gianvittorio.libraryapi.libraryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    @NotEmpty
    private String isbn;

    @NotEmpty
    private String customer;

    private Long id;

    private BookDTO book;

    @NotEmpty
    private String email;
}
