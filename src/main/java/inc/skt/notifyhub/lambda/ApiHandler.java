package inc.skt.notifyhub.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.skt.notifyhub.dto.HealthResponse;
import inc.skt.notifyhub.dto.NotificationRequest;
import inc.skt.notifyhub.dto.NotificationResponse;
import inc.skt.notifyhub.service.NotificationService;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.Map;

@Named("apiHandler")
@IfBuildProfile("prod")
public class ApiHandler implements RequestHandler<Map<String, Object>, Object> {

    @Inject
    NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        String path = (String) input.get("path");
        String httpMethod = (String) input.get("httpMethod");
        
        context.getLogger().log("Path: " + path + ", Method: " + httpMethod);

        // Health endpoint
        if ("/health".equals(path) && "GET".equals(httpMethod)) {
            return new HealthResponse("UP", "notifyhub");
        }
        
        // Notification endpoint
        if ("/api/v1/notifications".equals(path) && "POST".equals(httpMethod)) {
            try {
                String bodyString = (String) input.get("body");
                context.getLogger().log("Request body: " + bodyString);
                NotificationRequest request = objectMapper.readValue(bodyString, NotificationRequest.class);
                return notificationService.sendNotification(request);
            } catch (Exception e) {
                context.getLogger().log("Error processing notification: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to process notification", e);
            }
        }
        
        throw new RuntimeException("Unsupported path: " + path);
    }
}
