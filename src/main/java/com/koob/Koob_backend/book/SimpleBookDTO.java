package com.koob.Koob_backend.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SimpleBookDTO {
    private String googleBookId;
    private String title;
    private List<String> authors;
    private String description;
}
