package co.com.telefonica.prepago.publisher.service;

import co.com.telefonica.prepago.publisher.dto.RequestMessageDto;
import co.com.telefonica.prepago.publisher.dto.SubjectLoggerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NatsPublisherService {
    @Value("${nats.subjectLogger}")
    private String subjectLogger;

    private final Connection natsConnection;

    @Autowired
    public NatsPublisherService(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    public void publishMessage(RequestMessageDto dto) {
        natsConnection.publish(dto.getSubject(), dto.getMessage().getBytes());
    }

    @SneakyThrows
    public void publishLogger(SubjectLoggerDTO dto) {
        dto.setHash(dto.generateHash());
        ObjectMapper objectMapper = new ObjectMapper();
        var valueAsString = objectMapper.writeValueAsString(dto);
        natsConnection.publish(subjectLogger, valueAsString.getBytes());
    }

}
