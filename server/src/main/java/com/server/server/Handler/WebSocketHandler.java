package com.server.server.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.server.SecurityService;
import com.server.server.model.SecureMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final SecurityService securityService;
    private final ObjectMapper mapper = new ObjectMapper();
    private int lastSeqReceived = 0;

    public WebSocketHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("✅ Client connected to server");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        SecureMessage msg = mapper.readValue(message.getPayload(), SecureMessage.class);

        String expectedChecksum = securityService.computeChecksum(msg.getSeq(), msg.getPayload());
        if (!expectedChecksum.equals(msg.getChecksum())) {
            System.err.println("❌ Tampered message detected!");
            return;
        }

        if (msg.getSeq() <= lastSeqReceived) {
            System.err.println("⚠️ Replay or out-of-order message ignored");
            return;
        }

        lastSeqReceived = msg.getSeq();
        System.out.println("📩 Received from client: " + msg.getPayload());

        // Send acknowledgment
        String responsePayload = "Ack for message #" + msg.getSeq();
        String checksum = securityService.computeChecksum(msg.getSeq(), responsePayload);
        SecureMessage reply = new SecureMessage(msg.getSeq(), responsePayload, checksum);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(reply)));
    }
}
