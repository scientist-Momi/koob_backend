package com.koob.Koob_backend.libraryItem;

import lombok.Data;

@Data
public class GetBooksRequest {
    private long libraryId;
    private long userId;
}
