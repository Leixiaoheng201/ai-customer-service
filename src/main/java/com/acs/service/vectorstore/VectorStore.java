package com.acs.service.vectorstore;

import java.util.List;

public interface VectorStore {
    void add(Long id, float[] embedding, String text);
    List<SearchResult> search(float[] queryEmbedding, int topK);
    void remove(Long id);

    record SearchResult(Long id, float score) {}
}
