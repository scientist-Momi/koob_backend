package com.koob.Koob_backend.libraryItem;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.book.BookRepository;
import com.koob.Koob_backend.library.Library;
import com.koob.Koob_backend.library.LibraryRepository;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserRepository;
import com.koob.Koob_backend.userLibrary.UserLibrary;
import com.koob.Koob_backend.userLibrary.UserLibraryMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryItemService {
    private final LibraryItemRepository libraryItemRepository;
    private final LibraryRepository libraryRepository;
    private final UserLibraryMapper userLibraryMapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LibraryItemService(LibraryItemRepository libraryItemRepository, LibraryRepository libraryRepository, UserLibraryMapper userLibraryMapper, UserRepository userRepository, BookRepository bookRepository) {
        this.libraryItemRepository = libraryItemRepository;
        this.libraryRepository = libraryRepository;
        this.userLibraryMapper = userLibraryMapper;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void addBookToLibrary(Long userId, Book book, Long libraryId){
        Library lib = libraryRepository.findByIdAndUserId(libraryId, userId)
                .orElseThrow(() -> new RuntimeException("Library not found or does not belong to user"));

        Optional<LibraryItem> bookAlreadyExists = libraryItemRepository.findByLibraryIdAndBookId(libraryId, book.getId());
        if(bookAlreadyExists.isEmpty()){
            LibraryItem entry = new LibraryItem();
            entry.setBook(book);
            entry.setLibrary(lib);
            entry.setStatus("to-read");
            libraryItemRepository.save(entry);
        }
    }


}
