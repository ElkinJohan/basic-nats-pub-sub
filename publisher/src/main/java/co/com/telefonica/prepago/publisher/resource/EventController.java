package co.com.telefonica.prepago.publisher.resource;

import co.com.telefonica.prepago.publisher.dto.RequestMessageDto;
import co.com.telefonica.prepago.publisher.dto.SubjectLoggerDTO;
import co.com.telefonica.prepago.publisher.dto.SubjectLoggerV100;
import co.com.telefonica.prepago.publisher.service.NatsPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${controller.properties.base-path}")
public class EventController {

    private final NatsPublisherService natsPublisher;

    @Autowired
    public EventController(NatsPublisherService natsPublisher) {
        this.natsPublisher = natsPublisher;
    }

    @PostMapping("/message")
    public void publishEvent(@RequestBody RequestMessageDto event) {
        natsPublisher.publishMessage(event);
    }
    @PostMapping("/logger")
    public void publishLoggerSubject(@RequestBody SubjectLoggerDTO event) {
       // event.setTransactionId(UUID.randomUUID());
        natsPublisher.publishLogger(event);
    }
    @PostMapping("/loggerV100")
    public void publishLoggerSubjectV100(@RequestBody SubjectLoggerV100 event) {
        // event.setTransactionId(UUID.randomUUID());
        natsPublisher.publishLoggerV100(event);
    }
}
