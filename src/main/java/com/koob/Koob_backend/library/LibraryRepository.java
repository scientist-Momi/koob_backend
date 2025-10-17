package com.koob.Koob_backend.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {
    List<Library> findByUserId(Long userId);
    Optional<Library> findByShareCode(String shareCode);
    Optional<Library> findByIdAndUserId(Long libraryId, Long userId);

}
