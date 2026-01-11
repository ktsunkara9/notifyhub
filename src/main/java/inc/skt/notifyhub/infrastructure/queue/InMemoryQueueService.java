package inc.skt.notifyhub.infrastructure.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ApplicationScoped
public class InMemoryQueueService {

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessage(String queueUrl, Object message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            queue.offer(messageBody);
            System.out.println("✅ Queued to in-memory queue: " + messageBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to queue message", e);
        }
    }

    public String receiveMessage() {
        return queue.poll();
    }

    public int getQueueSize() {
        return queue.size();
    }
}
