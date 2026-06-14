package com.callerid.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class WalkieTalkieHandler extends BinaryWebSocketHandler {

    // Lista thread-safe para almacenar las sesiones activas de los usuarios en el Walkie-Talkie
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("🎙️ [Walkie-Talkie] Nuevo dispositivo conectado. ID Sesión: " + session.getId());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        // Transmisión en tiempo real: Tomamos el paquete de audio binario entrante
        byte[] audioBuffer = message.getPayload().array();

        // Hacemos un multicast a todos los usuarios conectados en la comunidad, excepto al que está hablando
        synchronized (sessions) {
            for (WebSocketSession activeSession : sessions) {
                if (activeSession.isOpen() && !activeSession.getId().equals(session.getId())) {
                    try {
                        activeSession.sendMessage(new BinaryMessage(audioBuffer));
                    } catch (IOException e) {
                        System.err.println("Error retransmitiendo audio a la sesión " + activeSession.getId() + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("❌ [Walkie-Talkie] Conexión cerrada para la sesión: " + session.getId() + " - Razón: " + status.getReason());
    }
}
