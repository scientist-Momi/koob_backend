package com.koob.Koob_backend.library;

import com.koob.Koob_backend.user.User;
import com.koob.Koob_backend.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;

    public LibraryService(LibraryRepository libraryRepository, UserRepository userRepository) {
        this.libraryRepository = libraryRepository;
        this.userRepository = userRepository;
    }

    public List<Library> getLibrariesByUser(Long userId) {
        return libraryRepository.findByUserId(userId);
    }

    public Library createLibrary(NewLibraryRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Library library = new Library();
        library.setName(request.getName());
        library.setUser(user);
        library.setPrivate(request.isPrivate());

        if (!request.isPrivate()) {
            library.setShareCode(UUID.randomUUID().toString());
        }

        return libraryRepository.save(library);
    }
}
