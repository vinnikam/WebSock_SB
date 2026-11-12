package co.vinni.dmos.server;

import co.vinni.dmos.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class NetworkSocketHandler extends TextWebSocketHandler {

    // Mapa: Sala -> Conjunto de sesiones conectadas a esa sala
    private static final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    // Mapa: SessionID -> Sala actual del usuario (para limpieza rápida al desconectar)
    private static final Map<String, String> sessionRoomMap = new ConcurrentHashMap<>();

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NetworkSocketHandler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        roomSessions.put("general", new CopyOnWriteArraySet<>());
        roomSessions.put("futbol", new CopyOnWriteArraySet<>());
        roomSessions.put("variedades", new CopyOnWriteArraySet<>());
        roomSessions.put("musica", new CopyOnWriteArraySet<>());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String room = extractRoomFromSession(session);
        joinRoom(session, room);

        System.out.println("🔌 [SPRING-SERVER] Nodo conectado (" + session.getId() + ") en sala: " + room);
        ChatMessage welcome = new ChatMessage(room, "Sistema", "Conectado a la sala #" + room);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(welcome)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        if ("PING".equalsIgnoreCase(payload)) {
            session.sendMessage(new TextMessage("PONG"));
            return;
        }

        // Parsear el JSON recibido del cliente
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        String room = chatMessage.getSala().toLowerCase();

        // Si el usuario cambió de sala, actualizar su registro local
        String currentRoom = sessionRoomMap.get(session.getId());
        if (!room.equals(currentRoom)) {
            leaveRoom(session);
            joinRoom(session, room);
        }

        // Publicar el mensaje en el canal de Redis correspondiente
        String channel = "chat:" + room;
        redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(chatMessage));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        leaveRoom(session);
        System.out.println("❌ [SPRING-SERVER] Nodo desconectado: " + session.getId());
    }

    // Método invocado por RedisMessageSubscriber cuando llega un mensaje desde Redis
    public void broadcastToRoom(ChatMessage chatMessage) {
        Set<WebSocketSession> sessions = roomSessions.get(chatMessage.getSala().toLowerCase());
        if (sessions == null) return;

        try {
            String jsonMessage = objectMapper.writeValueAsString(chatMessage);
            TextMessage textMessage = new TextMessage(jsonMessage);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            }
        } catch (IOException e) {
            System.err.println("Error difundiendo mensaje en sala: " + e.getMessage());
        }
    }

    private void joinRoom(WebSocketSession session, String room) {
        roomSessions.computeIfAbsent(room, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionRoomMap.put(session.getId(), room);
    }

    private void leaveRoom(WebSocketSession session) {
        String room = sessionRoomMap.remove(session.getId());
        if (room != null && roomSessions.containsKey(room)) {
            roomSessions.get(room).remove(session);
        }
    }

    private String extractRoomFromSession(WebSocketSession session) {
        if (session.getUri() != null && session.getUri().getQuery() != null) {
            String query = session.getUri().getQuery(); // ej: room=futbol
            if (query.startsWith("room=")) {
                return query.substring(5).toLowerCase();
            }
        }
        return "general";
    }
}
