package com.koob.Koob_backend.libraryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
    List<LibraryItem> findByLibraryId(Long libraryId);
    Optional<LibraryItem> findByLibraryIdAndBookId(Long libraryId, Long bookId);
    boolean existsByLibraryIdAndBookId(Long libraryId, Long bookId);
    void deleteByLibraryIdAndBookId(Long libraryId, Long bookId);
}
