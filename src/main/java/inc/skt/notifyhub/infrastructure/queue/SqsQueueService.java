package inc.skt.notifyhub.infrastructure.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@ApplicationScoped
public class SqsQueueService {

    @Inject
    SqsClient sqsClient; // Quarkus will now inject the native-optimized client

    @Inject
    ObjectMapper objectMapper; // Use the optimized Jackson mapper

    // No-args constructor for CDI
    public SqsQueueService() {}

    public void sendMessage(String queueUrl, Object message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();

            sqsClient.sendMessage(request);
        } catch (Exception e) {
            // Log the actual cause to help debugging
            throw new RuntimeException("SQS Send Error: " + e.getMessage(), e);
        }
    }
}