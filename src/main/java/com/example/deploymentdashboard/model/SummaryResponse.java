package com.example.deploymentdashboard.model;

public record SummaryResponse(
        long totalServices,
        long active,
        long failed,
        long uat,
        long prod
) {
}
