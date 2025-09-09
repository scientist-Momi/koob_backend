package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.book.BookRepository;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserLibraryService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserLibraryService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void addBookToUser(Long userId, Book book) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // check if the book already exists
        Book existingBook = bookRepository.findByGoogleBookId(book.getGoogleBookId())
                .orElseGet(() -> bookRepository.save(book));

        user.getBooks().add(existingBook);
        userRepository.save(user);
    }
}
