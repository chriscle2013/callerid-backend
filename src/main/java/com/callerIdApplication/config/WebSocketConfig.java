package com.callerid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WalkieTalkieHandler walkieTalkieHandler;

    // Inyectamos el manejador que procesará el audio en tiempo real
    public WebSocketConfig(WalkieTalkieHandler walkieTalkieHandler) {
        this.walkieTalkieHandler = walkieTalkieHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Definimos la ruta de conexión para el Walkie-Talkie y permitimos todas las IPs
        registry.addHandler(walkieTalkieHandler, "/walkietalkie")
                .setAllowedOrigins("*");
    }
}
