package inc.skt.notifyhub.resource;

import inc.skt.notifyhub.dto.NotificationRequest;
import inc.skt.notifyhub.dto.NotificationResponse;
import inc.skt.notifyhub.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/notifications")
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendNotification(NotificationRequest request) {
        // Fail-fast validation - throw exceptions
        if (request.userId == null || request.userId.trim().isEmpty()) {
            throw new IllegalArgumentException("userId is required");
        }
        
        if (request.message == null || request.message.trim().isEmpty()) {
            throw new IllegalArgumentException("message is required");
        }
        
        // Delegate to service
        NotificationResponse response = notificationService.sendNotification(request);
        
        return Response.status(Response.Status.ACCEPTED)
                .entity(response)
                .build();
    }
}
