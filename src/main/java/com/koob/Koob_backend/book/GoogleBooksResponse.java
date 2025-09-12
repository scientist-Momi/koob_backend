package com.koob.Koob_backend.book;

import lombok.Data;

import java.util.List;

@Data
public class GoogleBooksResponse {
    private String kind;
    private int totalItems;
    private List<GoogleBookItem> items;
}
