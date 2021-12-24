package com.example.securingweb.handler;

import com.example.securingweb.model.ws.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    private List<WebSocketSession> sessionList = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        System.out.printf("Enter : %s%n", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.printf("Send msg : %s : %s %n", session, message.getPayload());
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        for (WebSocketSession ws : sessionList) {
            TextMessage msg = new TextMessage(objectMapper.writeValueAsString(chatMessage));
            ws.sendMessage(msg);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionList.remove(session);
        System.out.printf("Exit : %s%n", session);
    }
}
