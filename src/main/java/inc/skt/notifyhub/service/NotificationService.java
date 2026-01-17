package inc.skt.notifyhub.service;

import inc.skt.notifyhub.dto.NotificationRequest;
import inc.skt.notifyhub.dto.NotificationResponse;
import inc.skt.notifyhub.infrastructure.queue.SqsQueueService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.UUID;

@ApplicationScoped
public class NotificationService {

    @Inject
    SqsQueueService queueService;

    @ConfigProperty(name = "notifyhub.sqs.queue-url")
    String queueUrl;

    public NotificationResponse sendNotification(NotificationRequest request) {
        String notificationId = UUID.randomUUID().toString();
        
        // Send to SQS
        queueService.sendMessage(queueUrl, request);
        
        NotificationResponse response = new NotificationResponse();
        response.notificationId = notificationId;
        response.status = "QUEUED";
        response.message = "Notification queued successfully";
        
        return response;
    }
}
