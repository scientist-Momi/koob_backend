package com.koob.Koob_backend.ai;

import com.koob.Koob_backend.book.*;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.userLibrary.UserLibraryService;
//import org.springframework.ai.tool.annotation.Tool;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor(force = true)
@Component
public class BookTools {
    private final BookService bookService;
    private final UserLibraryService libraryService;
    private static final Logger log = LogManager.getLogger(BookService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${google.books.api.key}")
    private String googleBooksApiKey;

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apiKey}";


    @Tool(description = "Use the google api to search for relevant books and articles related to user's query")
    public List<GoogleBookItem> searchForBooks(@ToolParam(description = "Query to be provided to the Google books api to retrieve relevant books and articles related to user's request. Provided in String format.") String query){
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

//    @Tool(description = "Search Google Books API for relevant books")
//    public List<SimpleBookDTO> searchForBooks(@ToolParam(description = "Search query") String query) {
//        List<GoogleBookItem> rawBooks = bookService.searchBooks(query);
//
//        return rawBooks.stream()
//                .map(item -> {
//                    VolumeInfo info = item.getVolumeInfo();
//                    String author = (info.getAuthors() != null && !info.getAuthors().isEmpty())
//                            ? String.join(", ", info.getAuthors())
//                            : "Unknown";
//                    return new SimpleBookDTO(
//                            info.getTitle(),
//                            author,
//                            info.getPublishedDate(),
//                            info.getDescription(),
//                            info.getInfoLink()
//                    );
//                })
//                .collect(Collectors.toList());
//    }


    @Tool(description = "Write results into a file for user")
    public static void writeToFile(@ToolParam(description = "Name of file to be saved, name of the file should be the query topic concatenated, provided in filename.txt format") String filename, @ToolParam(description = "Contents to be saved in the file, provide as a string") String content) {
        Path path = Path.of(filename);

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.writeString(path, content, StandardOpenOption.WRITE);
            System.out.println("✅ File written successfully: " + filename);
        } catch (IOException e) {
            System.err.println("❌ Error writing to file: " + e.getMessage());
        }
    }






















//    @Tool("Search for books or articles by query")
////    public List<GoogleBookItem> searchBooks(String query) {
////        return bookService.searchBooks(query); // you already have this
////    }
//    public List<Book> searchBooks(String query) {
//        return bookService.searchBooks(query).stream()
//                .map(bookService::mapGoogleBookToEntity)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    @Tool("Save the given books to the authenticated user's library")
//    public String saveToLibrary(List<Book> books) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = auth.getPrincipal();
//        if (!(principal instanceof User user)) {
//            throw new RuntimeException("User not authenticated or principal type mismatch");
//        }
//        libraryService.addBooksToUser(user.getId(), books);
//        return "Saved " + books.size() + " books to library";
//    }
//
//    @Tool
//    public void addBooksToUserLibrary(List<Book> books) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Object principal = auth.getPrincipal();
//        if (!(principal instanceof User user)) {
//            throw new RuntimeException("User not authenticated or principal type mismatch");
//        }
//        libraryService.addBooksToUser(user.getId(), books);
//    }

}
