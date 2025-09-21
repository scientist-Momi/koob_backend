package com.koob.Koob_backend.embedding;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.util.List;

@Service
public class BookEmbeddingService {
    private final OpenAiEmbeddingModel embeddingModel;
    private final EmbeddingStore<String> embeddingStore = new InMemoryEmbeddingStore<>();

    @Autowired
    public BookEmbeddingService(OpenAiEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    private Embedding generateEmbedding(String text) {
        return embeddingModel.embed(text).content();
    }

    public void saveBook(String id, String content) {
        Embedding embedding = generateEmbedding(content);
        embeddingStore.add(embedding, id);  // id = book identifier
    }

    public List<String> searchBooks(String query, int topK) {
        Embedding queryEmbedding = generateEmbedding(query);
        List<EmbeddingMatch<String>> matches = embeddingStore.findRelevant(queryEmbedding, topK);
        return matches.stream()
                .map(EmbeddingMatch::embedded) // gets your stored book ID
                .toList();
    }
}
