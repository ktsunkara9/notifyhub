package inc.skt.notifyhub.infrastructure.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@ApplicationScoped
public class SqsQueueService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public SqsQueueService() {
        this.sqsClient = SqsClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public void sendMessage(String queueUrl, Object message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build();
            
            sqsClient.sendMessage(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to SQS", e);
        }
    }
}
