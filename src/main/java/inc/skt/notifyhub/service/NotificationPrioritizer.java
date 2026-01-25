package inc.skt.notifyhub.service;

import inc.skt.notifyhub.dto.NotificationType;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class NotificationPrioritizer {

    @ConfigProperty(name = "queue.high-priority.url")
    String highPriorityQueueUrl;

    @ConfigProperty(name = "queue.low-priority.url")
    String lowPriorityQueueUrl;

    public String getQueueUrl(NotificationType type) {
        return isHighPriority(type) ? highPriorityQueueUrl : lowPriorityQueueUrl;
    }

    private boolean isHighPriority(NotificationType type) {
        return type == NotificationType.OTP || 
               type == NotificationType.ALERT || 
               type == NotificationType.TRANSACTIONAL;
    }
}
