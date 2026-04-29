package com.acs.service.controller;

import com.acs.service.AiCustomerServiceApplication;
import com.acs.service.llm.DemoLlmClient;
import com.acs.service.model.entity.HandoffRecord;
import com.acs.service.model.entity.QaPair;
import com.acs.service.model.enums.EmotionType;
import com.acs.service.model.enums.IntentType;
import com.acs.service.model.enums.RoutingTarget;
import com.acs.service.repository.HandoffRecordRepository;
import com.acs.service.repository.QaPairRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AiCustomerServiceApplication.class)
@AutoConfigureMockMvc
class CustomerServiceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HandoffRecordRepository handoffRepository;

    @Autowired
    private QaPairRepository qaRepository;

    @Autowired
    private DemoLlmClient demoLlmClient;

    @BeforeEach
    void setUp() {
        qaRepository.deleteAll();
        handoffRepository.deleteAll();
        demoLlmClient.clearHistory();
    }

    @Test
    void routingTest_angryCustomerRoutesToHuman() throws Exception {
        String conversation = "用户: 我已经等了3天了！你们系统有问题！立刻处理！\nAI: 退款将在3-5天内完成\n用户: 这是欺诈！我要投诉！";

        mockMvc.perform(post("/api/v1/customer/route")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(conversation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("target").value("HUMAN_AGENT"))
                .andExpect(jsonPath("intent").value("COMPLAINT"))
                .andExpect(jsonPath("emotion").value("ANGER"))
                .andExpect(jsonPath("emotionIntensity").value(closeTo(0.85f, 0.01f)));
    }

    @Test
    void contextCompressionTest() throws Exception {
        String conversation = "用户: 退款订单ORD-99887还没到账\nAI: 退款通常需要3-5个工作日\n用户: 已经第6天了，我需要加急处理";

        mockMvc.perform(post("/api/v1/customer/compress")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(conversation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("coreRequest").value("Customer needs help with refund status"))
                .andExpect(jsonPath("orderNumbers[0]").value("ORD-99887"))
                .andExpect(jsonPath("attemptedSolutions[0]").value("Provided standard refund timeline (3-5 business days)"))
                .andExpect(jsonPath("customerSentiment").value("Frustrated and urgent tone, escalating over multiple turns"));
    }

    @Test
    void dataClosureTest() throws Exception {
        // First create a handoff record
        HandoffRecord record = new HandoffRecord();
        record.setUserQuery("订单ORD-99887的退款还没到");
        record.setResolution("已加急处理，预计24小时内到账");
        record.setIntent(IntentType.REFUND_REQUEST);
        record = handoffRepository.save(record);

        // Simulate completion
        mockMvc.perform(post("/api/v1/customer/handoff/completed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record)))
                .andExpect(status().isOk());

        // Verify knowledge base updated
        mockMvc.perform(get("/api/v1/customer/knowledge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$[0].question").value("订单ORD-99887的退款还没到"));
    }
}