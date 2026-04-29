package com.acs.service.config;

import com.acs.service.llm.DemoLlmClient;
import com.acs.service.llm.LlmClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

    @Value("${acs.llm.demo-mode:true}")
    private boolean demoMode;

    @Bean
    public LlmClient llmClient() {
        if (demoMode) {
            return new DemoLlmClient();
        }
        // Production: return new OpenAiLlmClient(apiKey, model, baseUrl);
        return new DemoLlmClient();
    }
}
