package inc.skt.notifyhub.dto;

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
