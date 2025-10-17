package com.koob.Koob_backend.library;

import java.time.LocalDateTime;

public record LibraryDTO(
        Long id,
        String name,
        boolean isPrivate,
        String shareCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LibraryDTO fromEntity(Library library) {
        return new LibraryDTO(
                library.getId(),
                library.getName(),
                library.isPrivate(),
                library.getShareCode(),
                library.getCreatedAt(),
                library.getUpdatedAt()
        );
    }
}
