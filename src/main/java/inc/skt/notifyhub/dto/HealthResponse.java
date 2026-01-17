package inc.skt.notifyhub.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class HealthResponse {
    public String status;
    public String service;

    public HealthResponse() {
    }

    public HealthResponse(String status, String service) {
        this.status = status;
        this.service = service;
    }

    @Override
    public String toString() {
        return "HealthResponse{status='" + status + "', service='" + service + "'}";
    }
}
