package com.callerIdApplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.callerIdApplication.handler.WalkieTalkieHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Escucha directa en la raíz ("/") para evitar problemas con proxies de Render
        registry.addHandler(new WalkieTalkieHandler(), "/")
                .setAllowedOrigins("*");
        
        System.out.println("🚀 [PTT-INFRAESTRUCTURA] TÚNEL WEBSOCKET MONTADO DIRECTAMENTE EN LA RAÍZ (/)");
    }
}
