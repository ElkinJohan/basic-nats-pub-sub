package co.com.telefonica.prepago.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectLoggerV100 {
    private UUID transactionId;
    private String originService;
    private String request;
    private String response;
    private String duration;
}
