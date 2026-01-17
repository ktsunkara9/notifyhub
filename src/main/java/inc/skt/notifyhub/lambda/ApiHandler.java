package inc.skt.notifyhub.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import inc.skt.notifyhub.dto.HealthResponse;
import inc.skt.notifyhub.dto.NotificationRequest;
import inc.skt.notifyhub.service.NotificationService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Map;

@Named("apiHandler")
@RegisterForReflection
public class ApiHandler implements RequestHandler<Map<String, Object>, Object> {

    @Inject
    NotificationService notificationService;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Object handleRequest(Map<String, Object> input, Context context) {
        // 1. Robust Path Extraction (Checks rawPath first, then path)
        String path = (String) input.getOrDefault("rawPath", input.get("path"));

        // 2. Robust Method Extraction (Handles nested v2 structure)
        String httpMethod = "UNKNOWN";
        if (input.containsKey("requestContext")) {
            Map<String, Object> ctx = (Map<String, Object>) input.get("requestContext");
            if (ctx.containsKey("http")) {
                Map<String, Object> http = (Map<String, Object>) ctx.get("http");
                httpMethod = (String) http.get("method");
            }
        }
        if ("UNKNOWN".equals(httpMethod)) {
            httpMethod = (String) input.getOrDefault("httpMethod", "UNKNOWN");
        }

        context.getLogger().log("Processing - Path: " + path + ", Method: " + httpMethod);

        // 3. Routing Logic
        if ("/health".equals(path)) {
            return new HealthResponse("UP", "notifyhub");
        }

        if ("/api/v1/notifications".equals(path) && "POST".equalsIgnoreCase(httpMethod)) {
            try {
                Object bodyObj = input.get("body");
                NotificationRequest request;

                // Handle both String body and pre-parsed Map body
                if (bodyObj instanceof String) {
                    request = objectMapper.readValue((String) bodyObj, NotificationRequest.class);
                } else {
                    request = objectMapper.convertValue(bodyObj, NotificationRequest.class);
                }

                return notificationService.sendNotification(request);
            } catch (Exception e) {
                context.getLogger().log("Mapping Error: " + e.getMessage());
                throw new RuntimeException("Failed to parse request body", e);
            }
        }

        throw new RuntimeException("Unsupported path: " + path + " (Method: " + httpMethod + ")");
    }
}