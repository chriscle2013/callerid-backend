package com.callerid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// ⚡ IMPORTACIÓN CRUCIAL: Conectamos la configuración con la lógica del Walkie-Talkie
import com.callerid.handler.WalkieTalkieHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WalkieTalkieHandler walkieTalkieHandler;

    // El constructor ahora reconoce perfectamente la clase gracias al import superior
    public WebSocketConfig(WalkieTalkieHandler walkieTalkieHandler) {
        this.walkieTalkieHandler = walkieTalkieHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Habilitamos el endpoint de escucha para la app Android
        registry.addHandler(walkieTalkieHandler, "/walkietalkie")
                .setAllowedOrigins("*");
    }
}
