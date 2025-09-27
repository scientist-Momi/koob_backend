package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, Long> {
    List<UserLibrary> findByUser(User user);

    List<UserLibrary> findByUserId(Long userId);

    Optional<UserLibrary> findByUserAndBook(User user, Book book);

    void deleteByUserIdAndBookId(Long userId, Long bookId);

    Optional<UserLibrary> findByUserIdAndBookId(Long userId, Long bookId);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
