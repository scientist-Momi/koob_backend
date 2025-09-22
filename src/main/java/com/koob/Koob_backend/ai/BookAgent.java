package com.koob.Koob_backend.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface BookAgent {
    @SystemMessage("""
    You are a helpful book assistant. 
    Available tools:
    1. searchBooks(query) → finds relevant books/articles.
    2. saveToLibrary(books) → saves selected books.
    
    If user asks to find and save books, call the tools accordingly.
    Always explain what you did at the end.
    """)
    String chat(String userMessage);
}
