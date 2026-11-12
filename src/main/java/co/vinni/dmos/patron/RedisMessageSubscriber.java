package co.vinni.dmos.patron;
import co.vinni.dmos.dto.ChatMessage;
import co.vinni.dmos.server.NetworkSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisMessageSubscriber implements MessageListener {

    private final NetworkSocketHandler socketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageSubscriber(NetworkSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(json, ChatMessage.class);
            // Reenviar a las sesiones WebSocket locales suscritas a esa sala
            socketHandler.broadcastToRoom(chatMessage);
        } catch (Exception e) {
            System.err.println("Error procesando mensaje de Redis: " + e.getMessage());
        }
    }
}