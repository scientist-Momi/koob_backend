package com.koob.Koob_backend.book;

import com.koob.Koob_backend.userLibrary.UserLibraryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserLibraryService userLibraryService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}";

    public BookService(BookRepository bookRepository, BookMapper bookMapper, UserLibraryService userLibraryService) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.userLibraryService = userLibraryService;
    }

//    public List<BookDTO> searchBooks(String query) {
//        GoogleBooksResponse response = restTemplate.getForObject(
//                GOOGLE_BOOKS_API_URL,
//                GoogleBooksResponse.class,
//                query,
//                googleBooksApiKey
//        );
//
//        if (response == null || response.getItems() == null) {
//            return List.of();
//        }
//
//        return response.getItems().stream()
//                .map(this::mapGoogleBookToEntity)
//                .filter(Objects::nonNull)
//                .map(book -> bookMapper.toDto(book))
//                .collect(Collectors.toList());
//    }

    public List<GoogleBookItem> searchBooks(String query) {
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
                .filter(item -> item.getVolumeInfo() != null) // keep only valid items
                .collect(Collectors.toList());
    }


//    @Transactional
//    public BookDTO saveBookFromGoogle(GoogleBookItem item) {
//        return bookRepository.findByGoogleBookId(item.getId())
//                .map(bookMapper::toDto)
//                .orElseGet(() -> {
//                    Book book = mapGoogleBookToEntity(item);
//                    if (book == null) {
//                        throw new IllegalArgumentException("Book volume info is missing, cannot save");
//                    }
//                    Book saved = bookRepository.save(book);
//                    return bookMapper.toDto(saved);
//                });
//    }

    @Transactional
    public BookDTO saveBookFromGoogle(GoogleBookItem item, Long userId) {
        Book existingOrSaved = bookRepository.findByGoogleBookId(item.getId())
                .orElseGet(() -> {
                    Book book = mapGoogleBookToEntity(item);
                    if (book == null) {
                        throw new IllegalArgumentException("Book volume info is missing, cannot save");
                    }
                    return bookRepository.save(book);
                });

        // Add to user's library after ensuring the book exists
        if (userId != null) {
            userLibraryService.addBookToUser(userId, existingOrSaved);
        }

        return bookMapper.toDto(existingOrSaved);
    }



    private Book mapGoogleBookToEntity(GoogleBookItem item) {
        VolumeInfo info = item.getVolumeInfo();
        if (info == null) {
            return null; // skip items without volume info
        }

        return Book.builder()
                .googleBookId(item.getId())
                .title(info.getTitle() != null ? info.getTitle() : "Untitled")
                .subtitle(info.getSubtitle())
                .authors(info.getAuthors() != null ? info.getAuthors() : List.of())
                .publisher(info.getPublisher())
                .publishedDate(info.getPublishedDate())
                .description(info.getDescription())
                .pageCount(info.getPageCount())
                .thumbnailUrl(
                        (info.getImageLinks() != null) ? info.getImageLinks().getThumbnail() : null
                )
                .language(info.getLanguage())
                .previewLink(info.getPreviewLink())
                .infoLink(info.getInfoLink())
                .build();
    }


    private BookDTO mapGoogleBookToDTO(GoogleBookItem item) {
        Book book = mapGoogleBookToEntity(item);
        return (book != null) ? bookMapper.toDto(book) : null;
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
