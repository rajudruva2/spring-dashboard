package com.example.deploymentdashboard.model;

import java.util.List;

public record DeploymentPageResponse(
        int page,
        int pageSize,
        long totalRecords,
        int totalPages,
        List<Deployment> data
) {
}
