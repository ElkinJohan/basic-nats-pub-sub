package co.com.telefonica.prepago.subscriber.service.impl;

import co.com.telefonica.prepago.subscriber.persistence.entity.LoggerEntity;
import co.com.telefonica.prepago.subscriber.persistence.repository.ILoggerRepository;
import co.com.telefonica.prepago.subscriber.service.ILoggerService;
import io.nats.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Getter
@Setter
public class LoggerServiceImpl implements ILoggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerServiceImpl.class);
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ILoggerRepository iLoggerRepository;
    private final Connection natsConnection;
    private JetStreamSubscription subscription;

    @Value("${nats.subject}")
    private String subject;
    @Value("${nats.group}")
    private String group;
    @Value("${nats.consumer}")
    private String consumer;

    @Autowired
    public LoggerServiceImpl(ILoggerRepository iLoggerRepository, Connection natsConnection) {
        this.iLoggerRepository = iLoggerRepository;
        this.natsConnection = natsConnection;
    }

    @PostConstruct
    @Override
    public void start() {
        try {
            JetStream jetStream = natsConnection.jetStream();
            Dispatcher dispatcher = natsConnection.createDispatcher();

            subscription = jetStream.subscribe(subject, group, dispatcher, this::handleMessage, false, createPushSubscribeOptions());
            LOGGER.info("Subscribed to NATS successfully.");
        } catch (JetStreamApiException | IOException e) {
            LOGGER.error("Failed to subscribe to NATS.", e);
        }
    }

    protected void handleMessage(Message msg) {
        int count = this.counter.incrementAndGet();
        String json = new String(msg.getData(), StandardCharsets.UTF_8);
        LOGGER.info("Message {}: {}", count, json);

        var entity = LoggerEntity.builder().content(json).build();
        LoggerEntity save = this.iLoggerRepository.save(entity);
        if (save.getIdLogger() < 1) {
            LOGGER.error("API Error::data persistence error");
            return;
        }
        msg.ack();
    }

    private PushSubscribeOptions createPushSubscribeOptions() {
        return PushSubscribeOptions.builder()
                .durable(this.consumer)
                .deliverGroup(this.group)
                .build();
    }

    @PreDestroy
    @Override
    public void stop() {
        if (this.subscription != null) {
            try {
                this.subscription.drain(Duration.ofSeconds(1));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.natsConnection != null) {
            try {
                this.natsConnection.close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
