package com.koob.Koob_backend.book;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}";

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public List<BookDTO> searchBooks(String query) {
        GoogleBooksResponse response = restTemplate.getForObject(
                GOOGLE_BOOKS_API_URL,
                GoogleBooksResponse.class,
                query,
                googleBooksApiKey
        );

        if (response == null || response.getItems() == null) {
            return List.of();
        }

        return response.getItems().stream()
                .map(this::mapGoogleBookToDTO) // method in BookService
                .collect(Collectors.toList());

    }

    @Transactional
    public BookDTO saveBookFromGoogle(GoogleBookItem item) {
        return bookRepository.findByGoogleBookId(item.getId())
                .map(bookMapper::toDto) // already in DB, just map to DTO
                .orElseGet(() -> {
                    Book book = mapGoogleBookToEntity(item);
                    Book saved = bookRepository.save(book);
                    return bookMapper.toDto(saved);
                });
    }

    private Book mapGoogleBookToEntity(GoogleBookItem item) {
        var info = item.getVolumeInfo();

        Book book = new Book();
        book.setGoogleBookId(item.getId());
        book.setTitle(info.getTitle());
        book.setSubtitle(info.getSubtitle());
        book.setAuthors(info.getAuthors());
        book.setPublisher(info.getPublisher());
        book.setPublishedDate(info.getPublishedDate());
        book.setDescription(info.getDescription());
        book.setPageCount(info.getPageCount());
        book.setLanguage(info.getLanguage());
        book.setThumbnailUrl(info.getImageLinks() != null ? info.getImageLinks().getThumbnail() : null);
        book.setPreviewLink(info.getPreviewLink());
        book.setInfoLink(info.getInfoLink());

        return book;
    }

    private BookDTO mapGoogleBookToDTO(GoogleBookItem item) {
        Book book = mapGoogleBookToEntity(item);
        return bookMapper.toDto(book);
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

}
