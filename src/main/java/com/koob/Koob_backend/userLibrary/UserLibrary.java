package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_library")
public class UserLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime addedAt = LocalDateTime.now();

    // Optional: for future AI agents
    private String status; // e.g., "to-read", "reading", "finished"
    private Integer rating; // 1-5

    @Lob
    @Column(columnDefinition = "TEXT")
    private String notes;
}
