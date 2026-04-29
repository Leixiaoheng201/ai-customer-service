package com.acs.service.routing;

import com.acs.service.llm.LlmClient;
import com.acs.service.model.enums.IntentType;
import com.acs.service.model.enums.EmotionType;
import com.acs.service.model.enums.RoutingTarget;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class RoutingService {

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public RoutingService(LlmClient llmClient, ObjectMapper objectMapper) {
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    public RoutingDecision route(String conversationHistory) {
        String prompt = createRoutingPrompt(conversationHistory);
        String jsonResponse = llmClient.chat(prompt, 0.3);

        try {
            JsonNode node = objectMapper.readTree(jsonResponse);
            RoutingTarget target = RoutingTarget.valueOf(node.get("target").asText());
            IntentType intent = IntentType.valueOf(node.get("intent").asText());
            EmotionType emotion = EmotionType.valueOf(node.get("primaryEmotion").asText());
            float intensity = (float) node.get("emotionIntensity").asDouble();
            float confidence = (float) node.get("confidenceScore").asDouble();

            return new RoutingDecision(
                target,
                intent,
                emotion,
                intensity,
                confidence,
                node.get("reasoning").asText()
            );
        } catch (Exception e) {
            // Fallback to safe routing
            return new RoutingDecision(
                RoutingTarget.AI_AUTO,
                IntentType.GENERAL_INQUIRY,
                EmotionType.NEUTRAL,
                0.1f,
                0.8f,
                "Error parsing LLM response, defaulting to AI"
            );
        }
    }

    private String createRoutingPrompt(String conversation) {
        return "Analyze this customer conversation and determine routing decision in JSON format:\n" +
               "- target: AI_AUTO or HUMAN_AGENT\n" +
               "- intent: from IntentType enum\n" +
               "- primaryEmotion: from EmotionType enum\n" +
               "- emotionIntensity: float 0-1\n" +
               "- confidenceScore: float 0-1\n" +
               "- reasoning: brief explanation\n" +
               "Conversation:\n" + conversation;
    }

    public record RoutingDecision(
        RoutingTarget target,
        IntentType intent,
        EmotionType emotion,
        float emotionIntensity,
        float confidenceScore,
        String reasoning
    ) {}
}