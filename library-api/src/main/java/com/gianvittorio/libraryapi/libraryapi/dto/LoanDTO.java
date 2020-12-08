package com.gianvittorio.libraryapi.libraryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private String isbn;
    private String customer;
    private Long id;
    private BookDTO book;
}
