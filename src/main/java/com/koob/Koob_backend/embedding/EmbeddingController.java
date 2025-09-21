package com.koob.Koob_backend.embedding;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {
    private final BookEmbeddingService bookEmbeddingService;

    public EmbeddingController(BookEmbeddingService bookEmbeddingService) {
        this.bookEmbeddingService = bookEmbeddingService;
    }

    @PostMapping("/save")
    public String save(@RequestParam String id, @RequestBody String content) {
        bookEmbeddingService.saveBook(id, content);
        return "Saved embedding for book " + id;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        return bookEmbeddingService.searchBooks(query, 5);
    }
}
