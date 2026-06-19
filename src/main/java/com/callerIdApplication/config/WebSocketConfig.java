package com.callerid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// ⚡ IMPORTACIÓN CRUCIAL
import com.callerid.handler.WalkieTalkieHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Instanciamos el Handler directamente en el registro para evitar 
        // fallos en el orden de carga de Beans de Spring durante el arranque en Render.
        registry.addHandler(new WalkieTalkieHandler(), "/walkietalkie")
                .setAllowedOrigins("*");
        
        System.out.println("🚀 [PTT-INFRAESTRUCTURA] TÚNEL WEBSOCKET MONTADO EXITOSAMENTE EN /walkietalkie");
    }
}
