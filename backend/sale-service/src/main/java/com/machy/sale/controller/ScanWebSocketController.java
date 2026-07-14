package com.machy.sale.controller;

import com.machy.sale.service.ScanService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ScanWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ScanService scanService;

    public ScanWebSocketController(SimpMessagingTemplate messagingTemplate, ScanService scanService) {
        this.messagingTemplate = messagingTemplate;
        this.scanService = scanService;
    }

    @MessageMapping("/scan.join.{sessionId}")
    public void joinSession(@DestinationVariable String sessionId,
                            @Payload Map<String, String> message) {
        String type = message.get("type");
        if ("PIN".equals(type)) {
            String code = message.get("code");
            boolean ok = scanService.verifyPin(sessionId, code);
            String destination = "/topic/scan." + sessionId;
            if (ok) {
                messagingTemplate.convertAndSend(destination,
                    Map.of("type", "AUTH_OK", "message", "PIN correcto"));
            } else {
                messagingTemplate.convertAndSend(destination,
                    Map.of("type", "AUTH_FAIL", "message", "PIN incorrecto"));
            }
        }
    }

    @MessageMapping("/scan.code.{sessionId}")
    public void submitCode(@DestinationVariable String sessionId,
                           @Payload Map<String, String> message) {
        if (!scanService.isAuthenticated(sessionId)) {
            return;
        }
        String code = message.get("code");
        if (code != null && !code.isBlank()) {
            messagingTemplate.convertAndSend("/topic/scan." + sessionId,
                Map.of("type", "CODE", "code", code));
        }
    }
}
