package com.client.client.service;

import com.client.client.model.SecureMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebSocketClientService implements ApplicationRunner {
    private final SecurityService securityService;
    private final StandardWebSocketClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AtomicInteger sequenceNumber = new AtomicInteger(1);

    public WebSocketClientService(SecurityService securityService, StandardWebSocketClient client) {
        this.securityService = securityService;
        this.client = client;
    }

    @Override
    public void run(ApplicationArguments args) {
        connectToServer();
    }

    private void connectToServer() {
        WebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                System.out.println("✅ Connected to server");

                int seq = sequenceNumber.getAndIncrement();
                String payload = "Hello from client! I am sending message to the server";
                String checksum = securityService.computeChecksum(seq, payload);

                SecureMessage msg = new SecureMessage(seq, payload, checksum);
                String json = mapper.writeValueAsString(msg);

                session.sendMessage(new TextMessage(json));
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                System.out.println("📩 Received from server: " + message.getPayload());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                System.err.println("❌ WebSocket error: " + exception.getMessage());
            }
        };

        client.doHandshake(handler, null, URI.create("ws://localhost:8081/ws"))
                .addCallback(
                        result -> System.out.println("🤝 Handshake successful"),
                        ex -> System.err.println("❌ Handshake failed: " + ex.getMessage())
                );
    }
}
