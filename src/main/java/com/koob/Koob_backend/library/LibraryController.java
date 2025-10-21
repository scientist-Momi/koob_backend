package com.koob.Koob_backend.library;

import com.koob.Koob_backend.book.GoogleBookItem;
import com.koob.Koob_backend.libraryItem.GetBooksRequest;
import com.koob.Koob_backend.libraryItem.LibraryItemDTO;
import com.koob.Koob_backend.libraryItem.LibraryItemService;
import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boxes")
public class LibraryController {
    private final LibraryService libraryService;
    private final LibraryItemService libraryItemService;

    public LibraryController(LibraryService libraryService, LibraryItemService libraryItemService) {
        this.libraryService = libraryService;
        this.libraryItemService = libraryItemService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LibraryDTO>>> getBoxes(@AuthenticationPrincipal User user){
        List<Library> boxes = libraryService.getLibrariesByUser(user.getId());
        List<LibraryDTO> dtoList = boxes.stream()
                .map(LibraryDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("All boxes retrieved successfully", dtoList));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Library>> createBox(@RequestBody NewLibraryRequest request){
        Library box = libraryService.createLibrary(request);
        return ResponseEntity.ok(ApiResponse.success("Box created successfully", box));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<List<LibraryItemDTO>>> getLibraryItems(@RequestBody GetBooksRequest request){
        List<LibraryItemDTO> items = libraryItemService.getAllBooksInUserLibrary(request);
        return ResponseEntity.ok(ApiResponse.success("Box items retrieved", items));
    }

    @DeleteMapping("/{boxId}/book/{bookId}")
    public ResponseEntity<ApiResponse<Void>> removeBook(@PathVariable Long boxId, @PathVariable Long bookId){
        libraryService.removeBookFromLibrary(boxId, bookId);
        return ResponseEntity.ok(ApiResponse.success("Book removed", null));
    }
}
