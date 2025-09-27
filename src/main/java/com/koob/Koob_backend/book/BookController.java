package com.koob.Koob_backend.book;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.userLibrary.UserLibraryRepository;
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
    private final UserLibraryRepository userLibraryRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public BookController(UserLibraryRepository userLibraryRepository, BookRepository bookRepository, BookService bookService) {
        this.userLibraryRepository = userLibraryRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @GetMapping("/user/{userId}/book/{bookId}/recommendations")
    public ResponseEntity<ApiResponse<List<GoogleBookItem>>> getRecommendations(
            @PathVariable Long userId, @PathVariable Long bookId) {

        boolean ownsBook = userLibraryRepository.existsByUserIdAndBookId(userId, bookId);
        if (!ownsBook) {
            return ResponseEntity.status(403).body(ApiResponse.error("Book not in your library"));
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<GoogleBookItem> recs = bookService.getRecommendationsForUser(userId, book.getTitle(), book.getAuthors());

        return ResponseEntity.ok(ApiResponse.success("Recommendations found", recs));
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GoogleBookItem>>> searchBooks(@RequestParam("q") String query) {
        List<GoogleBookItem> results = bookService.searchBooks(query);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", results));
    }

    @GetMapping("/search2")
    public ResponseEntity<ApiResponse<List<SimpleBookDTO>>> searchBooks2(@RequestParam("q") String query) {
        List<SimpleBookDTO> results = bookService.searchBooks2(query);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", results));
    }


    @PostMapping("/save")
    public ResponseEntity<ApiResponse<BookDTO>> saveBook(@RequestBody GoogleBookItem item, @AuthenticationPrincipal User user
    ) {
        BookDTO saved = bookService.saveBookFromGoogle(item, user.getId());
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
