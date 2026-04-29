package com.acs.service.vectorstore;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InMemoryVectorStore implements VectorStore {

    private final Map<Long, float[]> embeddings = new ConcurrentHashMap<>();
    private final Map<Long, String> texts = new ConcurrentHashMap<>();

    @Override
    public void add(Long id, float[] embedding, String text) {
        embeddings.put(id, embedding.clone());
        texts.put(id, text);
        log.debug("[VectorStore] Added entry {} with embedding dim {}", id, embedding.length);
    }

    @Override
    public List<SearchResult> search(float[] queryEmbedding, int topK) {
        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<Long, float[]> entry : embeddings.entrySet()) {
            float score = cosineSimilarity(queryEmbedding, entry.getValue());
            results.add(new SearchResult(entry.getKey(), score));
        }
        results.sort((a, b) -> Float.compare(b.score(), a.score()));
        return results.subList(0, Math.min(topK, results.size()));
    }

    @Override
    public void remove(Long id) {
        embeddings.remove(id);
        texts.remove(id);
    }

    public String getText(Long id) {
        return texts.get(id);
    }

    public int size() {
        return embeddings.size();
    }

    private float cosineSimilarity(float[] a, float[] b) {
        float dot = 0, normA = 0, normB = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0 || normB == 0) return 0;
        return dot / ((float) Math.sqrt(normA) * (float) Math.sqrt(normB));
    }
}
