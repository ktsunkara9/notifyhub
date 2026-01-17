package inc.skt.notifyhub.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true) // Defensive: ignores extra fields in the JSON
public class NotificationRequest {

    public String userId;
    public String message;

    // 1. Explicit No-Args Constructor (Required for Jackson)
    public NotificationRequest() {
    }

    public NotificationRequest(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    // 2. Standard Getters and Setters (Ensures GraalVM sees the access points)
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}