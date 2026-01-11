package inc.skt.notifyhub.dto;

public class NotificationResponse {
    public String notificationId;
    public String status;
    public String message;

    @Override
    public String toString() {
        return "NotificationResponse{" +
                "notificationId='" + notificationId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
