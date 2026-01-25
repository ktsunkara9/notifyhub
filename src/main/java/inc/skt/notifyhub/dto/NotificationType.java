package inc.skt.notifyhub.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum NotificationType {
    OTP,           // High priority - time-sensitive one-time passwords
    ALERT,         // High priority - critical system alerts
    TRANSACTIONAL, // High priority - order confirmations, receipts
    PROMOTIONAL,   // Low priority - marketing messages
    INFORMATIONAL  // Low priority - newsletters, updates
}
