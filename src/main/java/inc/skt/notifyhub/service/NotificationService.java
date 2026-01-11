package inc.skt.notifyhub.service;

import inc.skt.notifyhub.dto.NotificationRequest;
import inc.skt.notifyhub.dto.NotificationResponse;
import inc.skt.notifyhub.infrastructure.queue.InMemoryQueueService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class NotificationService {

    @Inject
    InMemoryQueueService queueService;

    public NotificationResponse sendNotification(NotificationRequest request) {
        String notificationId = UUID.randomUUID().toString();
        
        // Send to queue (in-memory for local dev)
        queueService.sendMessage("local-queue", request);
        
        NotificationResponse response = new NotificationResponse();
        response.notificationId = notificationId;
        response.status = "PENDING";
        response.message = "Notification queued successfully";
        
        return response;
    }
}
