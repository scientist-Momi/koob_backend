package com.koob.Koob_backend.libraryItem;

import com.koob.Koob_backend.book.BookDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LibraryItemDTO {
    private Long id; // UserLibrary id
    private Long libraryId;
    private BookDTO book;
    private LocalDateTime createdAt;
    private String status;
    private Integer rating;
    private String notes;
}
