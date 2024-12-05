package co.com.telefonica.prepago.publisher.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class NatsConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(NatsConfig.class);

    @Value("${nats.url}")
    private String natsURL;
    @Value("${nats.maxReconnects}")
    private Integer maxReconnects;
    @Value("${nats.secondsReconnectWait}")
    private Integer secondsReconnectWait;

    @Bean
    public Connection natsConnection() throws IOException, InterruptedException {
        Options options = new Options.Builder()
                .server(natsURL)
                .maxReconnects(maxReconnects)
                .reconnectWait(Duration.ofSeconds(secondsReconnectWait))
                .build();

        Connection connection = Nats.connect(options);
        LOGGER.info("Connected to NATS at {}", natsURL);
        return connection;
    }
}

