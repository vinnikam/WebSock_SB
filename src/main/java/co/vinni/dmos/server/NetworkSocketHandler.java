package co.vinni.dmos.server;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class NetworkSocketHandler extends TextWebSocketHandler {

    // Lista thread-safe optimizada para concurrencia masiva en Spring
    private static final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("🔌 [SPRING-SERVER] Nuevo nodo de monitoreo conectado: " + session.getId());
        session.sendMessage(new TextMessage("CONEXIÓN CONFIGURADA CON SPRING BOOT: Escuchando red..."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("📥 [Paquete Recibido] en servidor (" + session.getId() + "): " + payload);

        // Caso 1: Mecanismo de Eco / Ping-Pong
        if ("PING".equalsIgnoreCase(payload)) {
            session.sendMessage(new TextMessage("PONG (Respuesta desde Spring Boot)"));
            return;
        }

        // Caso 2: Broadcast (Difusión masiva a todos los nodos de la red)
        broadcastMessage("📢 [ALERTA DE TRÁFICO]: " + payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("❌ [SPRING-SERVER] Nodo desconectado: " + session.getId());
    }

    private void broadcastMessage(String textMessage) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(textMessage));
                } catch (IOException e) {
                    System.err.println("Error enviando broadcast a " + session.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}
