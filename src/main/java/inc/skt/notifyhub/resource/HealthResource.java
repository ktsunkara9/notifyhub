package inc.skt.notifyhub.resource;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/health")
@IfBuildProfile("dev")
public class HealthResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthResponse check() {
        return new HealthResponse("UP", "NotifyHub is running");
    }
    
    public static class HealthResponse {
        public String status;
        public String message;
        
        public HealthResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}