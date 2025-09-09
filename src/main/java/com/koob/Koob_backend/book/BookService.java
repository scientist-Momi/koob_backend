package com.koob.Koob_backend.book;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }



}
