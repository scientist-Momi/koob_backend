package com.koob.Koob_backend.userLibrary;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.util.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class UserLibraryController {
    private final UserLibraryService userLibraryService;

    public UserLibraryController(UserLibraryService userLibraryService) {
        this.userLibraryService = userLibraryService;
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<UserLibraryDTO>>> getBooks(@AuthenticationPrincipal User user){
        List<UserLibraryDTO> library = userLibraryService.getUserBooks(user.getId());
        return ResponseEntity.ok(ApiResponse.success("User library retrieved successfully", library));
    }

    @PutMapping("/{userId}/book/{bookId}/notes")
    public ResponseEntity<ApiResponse<UserLibraryDTO>> updateNotes(
            @PathVariable Long userId,
            @PathVariable Long bookId,
            @RequestBody Map<String, Object> request
    ) {
        String notes = (String) request.get("notes"); // frontend sends JSON string
        UserLibraryDTO updated = userLibraryService.addOrUpdateNotes(userId, bookId, notes);
        return ResponseEntity.ok(ApiResponse.success("Notes updated", updated));
    }

    @DeleteMapping("/user/{userId}/book/{bookId}")
    public ResponseEntity<ApiResponse<Void>> removeBook(@PathVariable Long userId,
                                                        @PathVariable Long bookId) {
        userLibraryService.removeBookFromUser(userId, bookId);
        return ResponseEntity.ok(ApiResponse.success("Book removed", null));
    }

}
