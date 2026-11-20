package co.vinni.dmos.server;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication(scanBasePackages = "co.vinni.dmos")
public class NetworkApplication {
    private static final Logger log = LoggerFactory.getLogger(NetworkApplication.class);


    @Value("${server.port:8860}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(NetworkApplication.class, args);
    }

    // 2. Este método se ejecuta automáticamente justo después de que el servidor levanta
    @PostConstruct
    public void init() {
        log.info("\n🚀 Servidor de Redes con Spring Boot corriendo en: ws://localhost:" + port + "/network-monitor\n");
    }
}