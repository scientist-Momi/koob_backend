package com.koob.Koob_backend.book;

import lombok.Data;

import java.util.List;

@Data
public class NewBookRequest {
    private long libraryId;
    private List<GoogleBookItem> items;
}
