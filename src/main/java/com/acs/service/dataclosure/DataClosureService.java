package com.acs.service.dataclosure;

import com.acs.service.llm.LlmClient;
import com.acs.service.model.entity.HandoffRecord;
import com.acs.service.model.entity.QaPair;
import com.acs.service.repository.HandoffRecordRepository;
import com.acs.service.repository.QaPairRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DataClosureService {

    private final HandoffRecordRepository handoffRepository;
    private final QaPairRepository qaRepository;
    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public DataClosureService(HandoffRecordRepository handoffRepository,
                             QaPairRepository qaRepository,
                             LlmClient llmClient,
                             ObjectMapper objectMapper) {
        this.handoffRepository = handoffRepository;
        this.qaRepository = qaRepository;
        this.llmClient = llmClient;
        this.objectMapper = objectMapper;
    }

    public void processCompletedHandoff(Long handoffId) {
        Optional<HandoffRecord> optionalHandoff = handoffRepository.findById(handoffId);
        if (optionalHandoff.isEmpty()) return;

        HandoffRecord handoff = optionalHandoff.get();
        if (handoff.getResolution() == null || handoff.getResolution().isEmpty()) return;

        String evaluationResponse = llmClient.chat(createEvaluationPrompt(handoff), 0.1);
        try {
            JsonNode node = objectMapper.readTree(evaluationResponse);
            boolean isValuable = node.get("valuable").asBoolean();

            if (isValuable) {
                QaPair qaPair = new QaPair();
                qaPair.setQuestion(handoff.getUserQuery());
                qaPair.setAnswer(handoff.getResolution());
                qaPair.setCategory(handoff.getIntent().name());
                qaPair.setConfidenceScore((float) node.path("confidence").asDouble(0.8));
                qaRepository.save(qaPair);
            }
        } catch (Exception e) {
            // Log error but don't block processing
        }
    }

    public Iterable<QaPair> getAllQaPairs() {
        return qaRepository.findAll();
    }

    private String createEvaluationPrompt(HandoffRecord handoff) {
        return "Evaluate if this customer interaction contains valuable information for the knowledge base:\n" +
               "- Is the question common or high-frequency?\n" +
               "- Is the solution clear, verified, and actionable?\n" +
               "- Would storing this improve AI's future performance?\n" +
               "\nUser Query: " + handoff.getUserQuery() +
               "\nAgent Resolution: " + handoff.getResolution() +
               "\n\nRespond with JSON: {\"valuable\": boolean, \"confidence\": float 0-1, \"reason\": string}";
    }
}