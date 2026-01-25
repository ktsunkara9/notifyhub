package inc.skt.notifyhub.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.skt.notifyhub.dto.NotificationRequest;
import inc.skt.notifyhub.dto.NotificationType;
import inc.skt.notifyhub.infrastructure.queue.SqsQueueService;
import inc.skt.notifyhub.service.NotificationPrioritizer;
import inc.skt.notifyhub.service.NotificationValidator;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("validatorPrioritizerHandler")
@RegisterForReflection
public class ValidatorPrioritizerHandler implements RequestHandler<SQSEvent, Void> {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    NotificationValidator validator;

    @Inject
    NotificationPrioritizer prioritizer;

    @Inject
    SqsQueueService sqsQueueService;

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("Processing " + event.getRecords().size() + " messages from ingestion queue");

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                // Parse message body to NotificationRequest
                String body = message.getBody();
                NotificationRequest request = objectMapper.readValue(body, NotificationRequest.class);
                
                // Extract notification type
                NotificationType type = request.type;
                context.getLogger().log("Processing " + type + " notification for user: " + request.userId);
                
                // Validate notification
                validator.validate(request);
                context.getLogger().log("Validation passed for notification: " + request.userId);

                // Determine priority queue
                String queueUrl = prioritizer.getQueueUrl(type);
                context.getLogger().log("Routing to queue: " + queueUrl);

                // Send to appropriate priority queue
                sqsQueueService.sendMessage(queueUrl, request);
                context.getLogger().log("Message sent to priority queue for user: " + request.userId);

            } catch (Exception e) {
                context.getLogger().log("Error processing message: " + e.getMessage());
                // Let SQS handle retry logic - throw exception to mark as failed
                throw new RuntimeException("Failed to process message", e);
            }
        }

        return null;
    }
}
