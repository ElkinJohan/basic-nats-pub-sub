package co.com.telefonica.prepago.subscriber.service.impl;

import co.com.telefonica.prepago.subscriber.persistence.entity.LoggerEntity;
import co.com.telefonica.prepago.subscriber.persistence.repository.ILoggerRepository;
import io.nats.client.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggerServiceImplTest {
    @Mock
    private ILoggerRepository mockILoggerRepository;

    @Mock
    private Connection mockNatsConnection;

    @Mock
    private JetStream mockeJetStream;

    @Mock
    private Dispatcher mockDispatcher;
    @Mock
    private CompletableFuture<Boolean> mockDrain;

    @Mock
    private JetStreamSubscription mockSubscription;

    @InjectMocks
    private LoggerServiceImpl loggerService;

    @BeforeEach
    public void setUp() {
        String test = "test-string";
        loggerService.setSubject(test);
        loggerService.setGroup(test);
        loggerService.setConsumer(test);
    }

    // test to start method
    @Test
    void testStart_success() throws IOException, JetStreamApiException {
        when(mockNatsConnection.jetStream()).thenReturn(mockeJetStream);
        when(mockNatsConnection.createDispatcher()).thenReturn(mockDispatcher);
        when(mockeJetStream.subscribe(anyString(), anyString(), any(Dispatcher.class), any(), anyBoolean(), any(PushSubscribeOptions.class)))
                .thenReturn(mockSubscription);

        loggerService.start();

        verify(mockeJetStream).subscribe(anyString(), anyString(), any(Dispatcher.class), any(), anyBoolean(), any(PushSubscribeOptions.class));
        verify(mockNatsConnection).jetStream();
        verify(mockNatsConnection).createDispatcher();
    }

    @Test
    void testStart_JetStreamApiException() throws IOException, JetStreamApiException {
        when(mockNatsConnection.jetStream()).thenReturn(mockeJetStream);
        when(mockNatsConnection.createDispatcher()).thenReturn(mockDispatcher);
        when(mockeJetStream.subscribe(anyString(), anyString(), any(Dispatcher.class), any(), anyBoolean(), any(PushSubscribeOptions.class)))
                .thenThrow(JetStreamApiException.class);
        loggerService.start();
        verify(mockNatsConnection).jetStream();
        verify(mockNatsConnection).createDispatcher();
        verify(mockeJetStream).subscribe(anyString(), anyString(), any(Dispatcher.class), any(), anyBoolean(), any(PushSubscribeOptions.class));
    }

    @Test
    void testStart_IOException() throws IOException {
        when(mockNatsConnection.jetStream()).thenThrow(IOException.class);
        loggerService.start();
        verify(mockNatsConnection).jetStream();
    }

    // test to handleMessage method
    @Test
    void testHandleMessage_success() {
        Message message = mock(Message.class);
        when(message.getData()).thenReturn("{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8));
        when(mockILoggerRepository.save(any())).thenReturn(LoggerEntity.builder().idLogger(1L).build());

        loggerService.handleMessage(message);

        verify(mockILoggerRepository).save(any());
        verify(message).ack();
    }

    @Test
    void testHandleMessage_couldNotPersist() {
        Message message = mock(Message.class);
        when(message.getData()).thenReturn("{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8));
        when(mockILoggerRepository.save(any())).thenReturn(LoggerEntity.builder().idLogger(0L).build());

        loggerService.handleMessage(message);

        verify(mockILoggerRepository).save(any());
    }

    // test to stop method
    @Test
    void testStop_success() throws InterruptedException {
        loggerService.setSubscription(mockSubscription);
        when(mockSubscription.drain(any())).thenReturn(mockDrain);
        doNothing().when(mockNatsConnection).close();

        loggerService.stop();

        verify(mockSubscription).drain(any());
        verify(mockNatsConnection).close();
    }

    @Test
    void testStop_InterruptedException() throws InterruptedException {
        loggerService.setSubscription(mockSubscription);
        when(mockSubscription.drain(any())).thenThrow(InterruptedException.class);
        doThrow(InterruptedException.class).when(mockNatsConnection).close();

        loggerService.stop();

        verify(mockSubscription).drain(any());
        verify(mockNatsConnection).close();
    }

}