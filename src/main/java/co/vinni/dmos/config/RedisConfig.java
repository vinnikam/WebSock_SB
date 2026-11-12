package co.vinni.dmos.config;

import co.vinni.dmos.patron.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber subscriber) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Suscripción a las 4 salas definidas
        container.addMessageListener(subscriber, new ChannelTopic("chat:general"));
        container.addMessageListener(subscriber, new ChannelTopic("chat:futbol"));
        container.addMessageListener(subscriber, new ChannelTopic("chat:variedades"));
        container.addMessageListener(subscriber, new ChannelTopic("chat:musica"));

        return container;
    }
}