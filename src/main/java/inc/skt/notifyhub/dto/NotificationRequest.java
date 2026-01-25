package inc.skt.notifyhub.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true) // Defensive: ignores extra fields in the JSON
public class NotificationRequest {

    public String userId;
    public String message;
    public NotificationType type;

    public NotificationRequest() {
    }

    public NotificationRequest(String userId, String message, NotificationType type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
    }

    // 2. Standard Getters and Setters (Ensures GraalVM sees the access points)
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                '}';
    }
}