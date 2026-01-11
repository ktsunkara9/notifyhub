package inc.skt.notifyhub.exception;

public class ErrorResponse {
    public String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                '}';
    }
}
