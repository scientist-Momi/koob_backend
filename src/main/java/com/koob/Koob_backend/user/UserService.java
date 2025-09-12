package com.koob.Koob_backend.user;

import com.koob.Koob_backend.book.Book;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getOrCreateUser(String googleId, String email, String name, String pictureUrl) {
        return userRepository.findByGoogleId(googleId).orElseGet(() -> {
            User newUser = new User();
            newUser.setGoogleId(googleId);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPictureUrl(pictureUrl);
            return userRepository.save(newUser);
        });
    }

    public UserDTO updateUser(User user) {
        return userMapper.toDTO(userRepository.save(user));
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // Delete cookie
        ResponseCookie delete = ResponseCookie.from("AUTH-TOKEN", "")
                .httpOnly(true)
                .secure(false) // set true in prod
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", delete.toString());

        // Clear security context
        SecurityContextHolder.clearContext();

        // Invalidate session
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public Set<Book> getUserBooks(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getBooks();
    }
}
