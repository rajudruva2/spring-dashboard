package com.example.deploymentdashboard.model;

import java.time.LocalDateTime;

public record Deployment(
        long id,
        String name,
        String status,
        String version,
        String environment,
        LocalDateTime lastDeployment,
        String deployedBy,
        String buildNumber
) {
}
