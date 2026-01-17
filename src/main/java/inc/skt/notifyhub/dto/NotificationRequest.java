package inc.skt.notifyhub.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class NotificationRequest {
    public String userId;
    public String message;

    @Override
    public String toString() {
        return "NotificationRequest{" +
                "userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
