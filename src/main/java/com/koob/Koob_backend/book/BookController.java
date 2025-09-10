package com.koob.Koob_backend.book;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.userLibrary.UserLibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final UserLibraryService userLibraryService;

    public BookController(UserLibraryService userLibraryService) {
        this.userLibraryService = userLibraryService;
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveBookForUser(@RequestBody Book book, @AuthenticationPrincipal User user) {
        userLibraryService.addBookToUser(user.getId(), book);
        return ResponseEntity.ok("Book saved!");
    }

    @GetMapping("/mine")
    public ResponseEntity<Set<Book>> getMyBooks(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user.getBooks());
    }
}
