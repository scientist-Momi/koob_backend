package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.book.BookDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLibraryDTO {
    private Long id; // UserLibrary id
    private Long userId;
    private String userName;
    private BookDTO book;
    private LocalDateTime addedAt;
    private String status;
    private Integer rating;
}
