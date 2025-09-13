package com.koob.Koob_backend.book;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BookDTO {
    private Long id;
    private String googleBookId;
    private String title;
    private String subtitle;
    private List<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String thumbnailUrl;
    private Integer pageCount;
    private String language;
    private String previewLink;
    private String infoLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
