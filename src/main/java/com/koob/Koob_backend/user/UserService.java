package com.koob.Koob_backend.user;

import com.koob.Koob_backend.book.Book;
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

    public UserDTO getOrCreateUser(String googleId, String email, String name, String pictureUrl) {
        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setGoogleId(googleId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPictureUrl(pictureUrl);
                    return userRepository.save(newUser);
                });

        return userMapper.toDTO(user);
    }

    public UserDTO updateUser(User user) {
        return userMapper.toDTO(userRepository.save(user));
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public Set<Book> getUserBooks(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getBooks();
    }
}
