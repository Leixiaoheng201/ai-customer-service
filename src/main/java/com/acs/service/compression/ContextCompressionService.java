package com.acs.service.compression;

import com.acs.service.llm.LlmClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ContextCompressionService {

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public ContextCompressionService(LlmClient llmClient, ObjectMapper objectMapper) {
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    public HandoffContext compress(String conversationHistory) {
        String prompt = createCompressionPrompt(conversationHistory);
        String jsonResponse = llmClient.chat(prompt, 0.2);

        try {
            JsonNode node = objectMapper.readTree(jsonResponse);
            String coreRequest = node.get("coreRequest").asText();

            // Parse order numbers array
            String[] orderNumbers = new String[0];
            if (node.has("orderNumbers") && node.get("orderNumbers").isArray()) {
                orderNumbers = objectMapper.convertValue(node.get("orderNumbers"), String[].class);
            }

            String[] attemptedSolutions = new String[0];
            if (node.has("attemptedSolutions") && node.get("attemptedSolutions").isArray()) {
                attemptedSolutions = objectMapper.convertValue(node.get("attemptedSolutions"), String[].class);
            }

            String customerSentiment = node.has("customerSentiment") ? node.get("customerSentiment").asText() : "";
            String recommendedSteps = node.has("recommendedNextSteps") ? node.get("recommendedNextSteps").asText() : "";

            return new HandoffContext(
                coreRequest,
                orderNumbers,
                attemptedSolutions,
                customerSentiment,
                recommendedSteps
            );
        } catch (Exception e) {
            // Fallback to basic context
            return new HandoffContext(
                "Customer needs assistance",
                new String[0],
                new String[0],
                "Unknown sentiment",
                "Ask customer for more details"
            );
        }
    }

    private String createCompressionPrompt(String conversation) {
        return "Compress this customer conversation into structured context for handoff to human agent:\n" +
               "- coreRequest: main issue in 1 sentence\n" +
               "- orderNumbers: array of order IDs mentioned\n" +
               "- attemptedSolutions: array of solutions already tried\n" +
               "- customerSentiment: brief description of emotional state\n" +
               "- recommendedNextSteps: specific actions agent should take\n" +
               "\nConversation:\n" + conversation;
    }

    public record HandoffContext(
        String coreRequest,
        String[] orderNumbers,
        String[] attemptedSolutions,
        String customerSentiment,
        String recommendedNextSteps
    ) {}
}