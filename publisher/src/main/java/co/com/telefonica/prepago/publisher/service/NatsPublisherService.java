package co.com.telefonica.prepago.publisher.service;

import co.com.telefonica.prepago.publisher.dto.RequestMessageDto;
import co.com.telefonica.prepago.publisher.dto.SubjectLoggerDTO;
import co.com.telefonica.prepago.publisher.dto.SubjectLoggerV100;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NatsPublisherService {
    @Value("${nats.subjectLogger}")
    private String subjectLogger;
    @Value("${nats.subjectLoggerV100}")
    private String subjectLoggerV100;

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    public void publishMessage(RequestMessageDto dto) {
        natsConnection.publish(dto.getSubject(), dto.getMessage().getBytes());
    }

    @SneakyThrows
    public void publishLogger(SubjectLoggerDTO dto) {
        dto.setHash(dto.generateHash());
        var valueAsString = objectMapper.writeValueAsString(dto);
        natsConnection.publish(subjectLogger, valueAsString.getBytes());
    }

    @SneakyThrows
    public void publishLoggerV100(SubjectLoggerV100 dto) {
        var valueAsString = objectMapper.writeValueAsString(dto);
        natsConnection.publish(subjectLoggerV100, valueAsString.getBytes());
    }

}
