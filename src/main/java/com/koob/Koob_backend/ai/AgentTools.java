package com.koob.Koob_backend.ai;

import com.koob.Koob_backend.book.BookDTO;
import com.koob.Koob_backend.book.BookService;
import com.koob.Koob_backend.book.GoogleBookItem;
import com.koob.Koob_backend.user.User;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgentTools {
    private final BookService bookService;

    public AgentTools(BookService bookService) {
        this.bookService = bookService;
    }

    @Tool(description = "Use the google api to search for relevant books and articles related to user's query")
    public List<GoogleBookItem> getBooksFromGoogleBooks(@ToolParam(description = "Query to be provided to the Google books api to retrieve relevant books and articles related to user's request. Provided in String format." ) String query){
        return bookService.searchBooks(query);
    }

    @Tool(description = "Save all books retrieved from a query to a user library")
    public List<BookDTO> saveBooksForUser(@ToolParam(description = "List of Google Book IDs to save ") List<String> googleBookIds){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new RuntimeException("User is not authenticated");
        }

        User user = (User) authentication.getPrincipal();

        List<GoogleBookItem> bookItems = bookService.getBooksByIds(googleBookIds);
        return bookService.saveBooksFromGoogle(bookItems, user.getId());
    }
}
