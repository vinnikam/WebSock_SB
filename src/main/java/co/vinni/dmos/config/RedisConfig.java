package co.vinni.dmos.config;

import co.vinni.dmos.patron.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RedisConfig {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        if (connectionFactory instanceof LettuceConnectionFactory lettuce) {
            log.debug("=== DATOS REALES DE CONEXIÓN REDIS ===");
            log.debug("Host: " + lettuce.getHostName());
            log.debug("Port: " + lettuce.getPort());
            log.debug("SSL activo: " + lettuce.isUseSsl());
        }
            // Suscripción a las 4 salas definidas
        container.addMessageListener(subscriber, new ChannelTopic("chat:general"));
        container.addMessageListener(subscriber, new ChannelTopic("chat:futbol"));
        container.addMessageListener(subscriber, new ChannelTopic("chat:variedades"));
        container.addMessageListener(subscriber, new ChannelTopic("chat:musica"));

        return container;
    }
}