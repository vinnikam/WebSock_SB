package co.vinni.dmos.config;

import co.vinni.dmos.server.NetworkSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // Habilita la infraestructura de WebSockets de Spring
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NetworkSocketHandler(), "/network-monitor")
                .setAllowedOrigins("*"); // Permite conexiones desde cualquier origen/cliente
    }
}