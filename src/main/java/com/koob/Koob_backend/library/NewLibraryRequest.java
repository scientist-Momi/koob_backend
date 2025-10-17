package com.koob.Koob_backend.library;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewLibraryRequest {
    private String name;
    private Long userId;
    @JsonProperty("isPrivate")
    private boolean isPrivate;
}
