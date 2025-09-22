package com.koob.Koob_backend.ai;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.book.BookService;
import com.koob.Koob_backend.book.GoogleBookItem;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.userLibrary.UserLibraryService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookTools {
    private final BookService bookService;
    private final UserLibraryService libraryService;

    public BookTools(BookService bookService, UserLibraryService libraryService) {
        this.bookService = bookService;
        this.libraryService = libraryService;
    }

    @Tool("Search for books or articles by query")
    public List<GoogleBookItem> searchBooks(String query) {
        return bookService.searchBooks(query); // you already have this
    }

    @Tool("Save the given books to the authenticated user's library")
    public String saveToLibrary(List<Book> books) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new RuntimeException("User not authenticated or principal type mismatch");
        }
        libraryService.addBooksToUser(user.getId(), books);
        return "Saved " + books.size() + " books to library";
    }

}
