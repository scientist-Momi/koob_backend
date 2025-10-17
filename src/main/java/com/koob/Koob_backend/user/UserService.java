package com.koob.Koob_backend.user;

import com.koob.Koob_backend.book.Book;
import com.koob.Koob_backend.library.Library;
import com.koob.Koob_backend.library.LibraryRepository;
import com.koob.Koob_backend.library.LibraryService;
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
    private final LibraryRepository libraryRepository;

    public UserService(UserRepository userRepository, UserMapper userMapper, LibraryService libraryService, LibraryRepository libraryRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.libraryRepository = libraryRepository;
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
            userRepository.save(newUser);

            Library library = new Library();
            library.setUser(newUser);
            library.setName("Personal");
            library.setPrivate(true);
            libraryRepository.save(library);
            return newUser;
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
//                .domain(".oolumomi.dev")
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

//    public Set<Book> getUserBooks(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"))
//                .getBooks();
//    }
}
