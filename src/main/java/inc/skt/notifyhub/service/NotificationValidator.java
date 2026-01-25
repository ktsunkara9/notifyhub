package inc.skt.notifyhub.service;

import inc.skt.notifyhub.dto.NotificationRequest;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationValidator {

    public void validate(NotificationRequest request) {
        validateCommon(request);
        validateByType(request);
    }

    private void validateCommon(NotificationRequest request) {
        if (request.userId.length() > 50) {
            throw new IllegalArgumentException("userId must not exceed 50 characters");
        }
    }

    private void validateByType(NotificationRequest request) {
        switch (request.type) {
            case OTP -> validateOTP(request);
            case ALERT -> validateAlert(request);
            case TRANSACTIONAL -> validateTransactional(request);
            case PROMOTIONAL -> validatePromotional(request);
            case INFORMATIONAL -> validateInformational(request);
        }
    }

    private void validateOTP(NotificationRequest request) {
        if (request.message.length() > 160) {
            throw new IllegalArgumentException("OTP message must not exceed 160 characters");
        }
    }

    private void validateAlert(NotificationRequest request) {
        if (request.message.length() > 500) {
            throw new IllegalArgumentException("ALERT message must not exceed 500 characters");
        }
    }

    private void validateTransactional(NotificationRequest request) {
        if (request.message.length() > 1000) {
            throw new IllegalArgumentException("TRANSACTIONAL message must not exceed 1000 characters");
        }
    }

    private void validatePromotional(NotificationRequest request) {
        if (request.message.length() > 1000) {
            throw new IllegalArgumentException("PROMOTIONAL message must not exceed 1000 characters");
        }
    }

    private void validateInformational(NotificationRequest request) {
        if (request.message.length() > 2000) {
            throw new IllegalArgumentException("INFORMATIONAL message must not exceed 2000 characters");
        }
    }
}
