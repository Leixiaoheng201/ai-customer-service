package com.acs.service.controller;

import com.acs.service.compression.ContextCompressionService;
import com.acs.service.dataclosure.DataClosureService;
import com.acs.service.model.entity.HandoffRecord;
import com.acs.service.model.entity.QaPair;
import com.acs.service.routing.RoutingDecision;
import com.acs.service.routing.RoutingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerServiceController {

    private final RoutingService routingService;
    private final ContextCompressionService compressionService;
    private final DataClosureService dataClosureService;

    public CustomerServiceController(RoutingService routingService,
                                    ContextCompressionService compressionService,
                                    DataClosureService dataClosureService) {
        this.routingService = routingService;
        this.compressionService = compressionService;
        this.dataClosureService = dataClosureService;
    }

    @PostMapping("/route")
    public ResponseEntity<RoutingDecision> determineRouting(@RequestBody String conversationHistory) {
        return ResponseEntity.ok(routingService.route(conversationHistory));
    }

    @PostMapping("/compress")
    public ResponseEntity<ContextCompressionService.HandoffContext> compressContext(@RequestBody String conversationHistory) {
        return ResponseEntity.ok(compressionService.compress(conversationHistory));
    }

    @PostMapping("/handoff/completed")
    public ResponseEntity<Void> handleCompletedHandoff(@RequestBody HandoffRecord handoffRecord) {
        dataClosureService.processCompletedHandoff(handoffRecord.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/knowledge")
    public ResponseEntity<Iterable<QaPair>> getKnowledgeBase() {
        return ResponseEntity.ok(dataClosureService.getAllQaPairs());
    }
}