package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.book.BookRepository;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLibraryService {
    private final UserLibraryRepository userLibraryRepository;
    private final UserLibraryMapper userLibraryMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserLibraryService(UserLibraryRepository userLibraryRepository, UserLibraryMapper userLibraryMapper, UserRepository userRepository, BookRepository bookRepository) {
        this.userLibraryRepository = userLibraryRepository;
        this.userLibraryMapper = userLibraryMapper;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

//    @Transactional
//    public void addBookToUser(Long userId, Book book) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // check if the book already exists
//        Book existingBook = bookRepository.findByGoogleBookId(book.getGoogleBookId())
//                .orElseGet(() -> bookRepository.save(book));
//
//        user.getBooks().add(existingBook);
//        userRepository.save(user);
//    }

    @Transactional
    public void addBookToUser(Long userId, Book book) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book existingBook = bookRepository.findByGoogleBookId(book.getGoogleBookId())
                .orElseGet(() -> bookRepository.save(book));

        boolean alreadyExists = userLibraryRepository.findByUserAndBook(user, existingBook).isPresent();
        if (!alreadyExists) {
            UserLibrary entry = new UserLibrary();
            entry.setUser(user);
            entry.setBook(existingBook);
            userLibraryRepository.save(entry);
        }
    }

    public List<UserLibraryDTO> getUserBooks(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userLibraryRepository.findByUser(user).stream()
                .map(userLibraryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeBookFromUser(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        userLibraryRepository.deleteByUserAndBook(user, book);
    }

    @Transactional
    public UserLibraryDTO addOrUpdateNotes(Long userId, Long bookId, String notes) {
        UserLibrary userLibrary = userLibraryRepository
                .findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new RuntimeException("UserLibrary entry not found"));

        userLibrary.setNotes(notes);
        UserLibrary saved = userLibraryRepository.save(userLibrary);

        return userLibraryMapper.toDto(saved);
    }

//    use it to get all notes from all users on a particular book
//    @Transactional
//    public String getNotes(Long userId, Long bookId) {
//        return userLibraryRepository
//                .findByUserIdAndBookId(userId, bookId)
//                .map(UserLibrary::getNotes)
//                .orElse(null);
//    }
}
