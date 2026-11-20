package co.vinni.dmos.patron;
import co.vinni.dmos.dto.ChatMessage;
import co.vinni.dmos.server.NetworkSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RedisMessageSubscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RedisMessageSubscriber.class);


    private final NetworkSocketHandler socketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisMessageSubscriber(NetworkSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            ChatMessage chatMessage = objectMapper.readValue(json, ChatMessage.class);
            // Reenviar a las sesiones WebSocket locales suscritas a esa sala
            socketHandler.broadcastToRoom(chatMessage);
        } catch (Exception e) {
            log.error("Error procesando mensaje de Redis: " + e.getMessage());
        }
    }
}