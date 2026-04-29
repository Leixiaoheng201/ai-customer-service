package com.acs.service.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Slf4j
public class DemoLlmClient implements LlmClient {

    private static final int EMBEDDING_DIM = 128;

    @Override
    public String chat(String prompt, double temperature) {
        log.info("[Demo LLM] chat() called with temperature: {}", temperature);
        log.debug("[Demo LLM] Prompt length: {} chars", prompt.length());

        // Simulate LLM responses based on prompt content
        String lowerPrompt = prompt.toLowerCase();

        if (lowerPrompt.contains("routing") && lowerPrompt.contains("json")) {
            return simulateRoutingDecision(prompt);
        }
        if (lowerPrompt.contains("compress") && lowerPrompt.contains("json")) {
            return simulateCompressionResponse(prompt);
        }
        if (lowerPrompt.contains("ai response") || lowerPrompt.contains("answer")) {
            return simulateAiResponse(prompt);
        }
        if (lowerPrompt.contains("evaluate") && lowerPrompt.contains("valuable")) {
            return "{\"valuable\": true, \"reason\": \"High-frequency question with clear resolution\"}";
        }

        return "{\"reply\": \"I understand your concern. Let me help you with that.\"}";
    }

    @Override
    public float[] embed(String text) {
        // Deterministic pseudo-embedding based on text hash
        byte[] hashBytes;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hashBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            hashBytes = text.getBytes(StandardCharsets.UTF_8);
        }

        float[] embedding = new float[EMBEDDING_DIM];
        // Seed from hash, then use a simple LCG for reproducibility
        long seed = 0;
        for (int i = 0; i < Math.min(hashBytes.length, 8); i++) {
            seed = (seed << 8) | (hashBytes[i] & 0xFF);
        }

        for (int i = 0; i < EMBEDDING_DIM; i++) {
            seed = (seed * 6364136223846793005L + 1) & 0xFFFFFFFFFFFFFFFFL;
            double val = ((seed >>> 33) & 0xFFFFFFFFL) / 4294967296.0;
            embedding[i] = (float) (val * 2.0 - 1.0);
        }

        // Normalize to unit vector
        float norm = 0;
        for (float v : embedding) norm += v * v;
        norm = (float) Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < EMBEDDING_DIM; i++) {
                embedding[i] /= norm;
            }
        }

        return embedding;
    }

    private String simulateRoutingDecision(String prompt) {
        String lower = prompt.toLowerCase();
        if (lower.contains("退款") || lower.contains("refund")) {
            return """
                {"target":"AI_AUTO","intent":"REFUND_REQUEST","primaryEmotion":"NEUTRAL","emotionIntensity":0.2,"confidenceScore":0.85,"reasoning":"Standard refund inquiry, calm tone."}
                """;
        }
        if (lower.contains("生气") || lower.contains("愤怒") || lower.contains("立刻") || lower.contains("now") || lower.contains("immediately")) {
            return """
                {"target":"HUMAN_AGENT","intent":"COMPLAINT","primaryEmotion":"ANGER","emotionIntensity":0.85,"confidenceScore":0.92,"reasoning":"User shows strong anger and urgency, requires human intervention."}
                """;
        }
        if (lower.contains("投诉") || lower.contains("complaint")) {
            return """
                {"target":"HUMAN_AGENT","intent":"COMPLAINT","primaryEmotion":"FRUSTRATION","emotionIntensity":0.75,"confidenceScore":0.88,"reasoning":"Complaint detected, user frustrated."}
                """;
        }
        return """
            {"target":"AI_AUTO","intent":"GENERAL_INQUIRY","primaryEmotion":"NEUTRAL","emotionIntensity":0.1,"confidenceScore":0.8,"reasoning":"General question, calm user."}
            """;
    }

    private String simulateCompressionResponse(String prompt) {
        return """
            {"coreRequest":"Customer needs help with refund status","orderNumbers":["ORD-99887"],"attemptedSolutions":["Provided standard refund timeline (3-5 business days)"],"customerSentiment":"Frustrated and urgent tone, escalating over multiple turns","recommendedNextSteps":"Check order refund status in backend system, escalate if processing delay is confirmed"}
            """;
    }

    private String simulateAiResponse(String prompt) {
        return """
            {"reply":"感谢您的耐心等待。关于您的退款问题，一般情况下退款会在3-5个工作日内到账。如需进一步帮助，请提供订单号，我将为您查询具体进度。"}
            """;
    }
}
