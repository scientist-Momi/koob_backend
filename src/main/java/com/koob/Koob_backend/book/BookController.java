package com.koob.Koob_backend.book;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.userLibrary.UserLibraryService;
import com.koob.Koob_backend.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final UserLibraryService userLibraryService;
    private final BookService bookService;

    public BookController(UserLibraryService userLibraryService, BookService bookService) {
        this.userLibraryService = userLibraryService;
        this.bookService = bookService;
    }

//    @PostMapping("/save")
//    public ResponseEntity<String> saveBookForUser(@RequestBody Book book, @AuthenticationPrincipal User user) {
//        userLibraryService.addBookToUser(user.getId(), book);
//        return ResponseEntity.ok("Book saved!");
//    }
//
//    @GetMapping("/mine")
//    public ResponseEntity<Set<Book>> getMyBooks(@AuthenticationPrincipal User user) {
//        return ResponseEntity.ok(user.getBooks());
//    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BookDTO>>> searchBooks(@RequestParam("q") String query) {
        List<BookDTO> results = bookService.searchBooks(query);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", results));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<BookDTO>> saveBook(@RequestBody GoogleBookItem item) {
        BookDTO saved = bookService.saveBookFromGoogle(item);
        return ResponseEntity.ok(ApiResponse.success("Book saved successfully", saved));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("All books retrieved successfully", books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDTO>> getBook(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", book));
    }
}
