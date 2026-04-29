package com.acs.service.llm;

public interface LlmClient {
    String chat(String prompt, double temperature);
    float[] embed(String text);
}
