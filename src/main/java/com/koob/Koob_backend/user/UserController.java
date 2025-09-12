package com.koob.Koob_backend.user;

import com.koob.Koob_backend.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        UserDTO dto = userMapper.toDTO(user);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }


    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> status(@AuthenticationPrincipal User user) {
        if (user != null) {
            return ResponseEntity.ok(
                    ApiResponse.success("User is logged in", user.getEmail())
            );
        } else {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }
    }

}
