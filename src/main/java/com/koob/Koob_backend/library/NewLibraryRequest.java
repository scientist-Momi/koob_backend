package com.koob.Koob_backend.library;

import lombok.Data;

@Data
public class NewLibraryRequest {
    private String name;
    private Long userId;
    private boolean isPrivate;
}
