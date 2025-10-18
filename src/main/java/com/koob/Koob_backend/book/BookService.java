package com.koob.Koob_backend.book;

import com.koob.Koob_backend.libraryItem.LibraryItemService;
import com.koob.Koob_backend.userLibrary.UserLibraryRepository;
import com.koob.Koob_backend.userLibrary.UserLibraryService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserLibraryService userLibraryService;
    private final LibraryItemService libraryItemService;
    private final UserLibraryRepository userLibraryRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    private static final Logger log = LogManager.getLogger(BookService.class);

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}";
    private final String GOOGLE_BOOKS_API_URL_WITH_IDS = "https://www.googleapis.com/books/v1/volumes/";
    private static final String GOOGLE_BOOKS_API_URL_FOR_RECOMMENDATION = "https://www.googleapis.com/books/v1/volumes?q={query}&maxResults=3&key={apiKey}";


    public BookService(BookRepository bookRepository, BookMapper bookMapper, UserLibraryService userLibraryService, LibraryItemService libraryItemService, UserLibraryRepository userLibraryRepository) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
        this.userLibraryService = userLibraryService;
        this.libraryItemService = libraryItemService;
        this.userLibraryRepository = userLibraryRepository;
    }

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

    public List<GoogleBookItem> getRecommendations(String title, List<String> authors) {
        // Build query string: prioritize author, then title
        StringBuilder queryBuilder = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            queryBuilder.append(title);
        }
        if (authors != null && !authors.isEmpty()) {
            queryBuilder.append("+inauthor:").append(authors.get(0));
        }

        String query = queryBuilder.toString();

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
                .filter(item -> item.getVolumeInfo() != null)
                .collect(Collectors.toList());
    }

    public List<GoogleBookItem> getRecommendationsForUser(Long userId, String title, List<String> authors) {
        List<GoogleBookItem> recs = getRecommendations(title, authors);

        List<String> ownedGoogleIds = userLibraryRepository.findByUserId(userId).stream()
                .map(entry -> entry.getBook().getGoogleBookId())
                .toList();

        return recs.stream()
                .filter(item -> item.getId() != null && !ownedGoogleIds.contains(item.getId()))
                .limit(3)
                .collect(Collectors.toList());
    }


    public List<SimpleBookDTO> searchBooks2(String query) {
        if (query == null || query.trim().isEmpty()) {
            log.warn("Query is null or empty");
            return List.of();
        }
        try{
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            log.info("Calling Google Books API with query: {}", encodedQuery);
        Map<String, Object> response = restTemplate.getForObject(
                GOOGLE_BOOKS_API_URL,
                Map.class,
                query,
                googleBooksApiKey
        );

        if (response == null || !response.containsKey("items")) {
            log.warn("No items found for query: {}", query);
            return List.of();
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        return items.stream().map(item -> {
            Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");

            SimpleBookDTO dto = new SimpleBookDTO();
            dto.setGoogleBookId((String) item.get("id"));
            dto.setTitle((String) volumeInfo.get("title"));
            dto.setAuthors((List<String>) volumeInfo.getOrDefault("authors", List.of()));
            dto.setDescription((String) volumeInfo.getOrDefault("description", ""));
            return dto;
        }).collect(Collectors.toList());
        } catch (RestClientException e) {
            log.error("Error calling Google Books API: {}", e.getMessage(), e);
            return List.of();
        }
    }


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

    @Transactional
    public BookDTO saveBookFromGoogleToLibrary(GoogleBookItem item, Long userId, Long libraryId) {
        Book existingOrSaved = bookRepository.findByGoogleBookId(item.getId())
                .orElseGet(() -> {
                    Book book = mapGoogleBookToEntity(item);
                    if (book == null) {
                        throw new IllegalArgumentException("Book volume info is missing, cannot save");
                    }
                    return bookRepository.save(book);
                });

        if (userId != null) {
            libraryItemService.addBookToLibrary(userId, existingOrSaved, libraryId);
        }
        return bookMapper.toDto(existingOrSaved);
    }

//    public List<BookDTO> saveBooksFromGoogleToLibrary(List<GoogleBookItem> items, Long userId, Long libraryId){
//        List<BookDTO> savedBooks = new ArrayList<>();
//    }

    @Transactional
    public List<BookDTO> saveBooksFromGoogle(List<GoogleBookItem> items, Long userId) {
        List<BookDTO> savedBooks = new ArrayList<>();

        for (GoogleBookItem item : items) {
            Book existingOrSaved = bookRepository.findByGoogleBookId(item.getId())
                    .orElseGet(() -> {
                        Book book = mapGoogleBookToEntity(item);
                        if (book == null) {
                            throw new IllegalArgumentException(
                                    "Book volume info is missing for ID: " + item.getId()
                            );
                        }
                        return bookRepository.save(book);
                    });

            if (userId != null) {
                userLibraryService.addBookToUser(userId, existingOrSaved);
            }

            savedBooks.add(bookMapper.toDto(existingOrSaved));
        }

        return savedBooks;
    }

    public List<GoogleBookItem> getBooksByIds(List<String> ids) {
        List<GoogleBookItem> items = new ArrayList<>();

        for (String id : ids) {
            try {
                GoogleBookItem item = restTemplate.getForObject(
                        GOOGLE_BOOKS_API_URL_WITH_IDS + id,
                        GoogleBookItem.class
                );
                if (item != null) {
                    items.add(item);
                }
            } catch (Exception e) {
                // log and skip invalid/unavailable book
                System.err.println("Failed to fetch book with ID: " + id + " - " + e.getMessage());
            }
        }

        return items;
    }


    public Book mapGoogleBookToEntity(GoogleBookItem item) {
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
