package com.example.deploymentdashboard.service;

import com.example.deploymentdashboard.model.Deployment;
import com.example.deploymentdashboard.model.DeploymentPageResponse;
import com.example.deploymentdashboard.model.SummaryResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DeploymentService {

    private final List<Deployment> deployments = new ArrayList<>();

    private static final String[] SERVICE_NAMES = {
            "Claims Service",
            "Policy Service",
            "Customer Service",
            "Payment Service",
            "Notification Service",
            "Document Service",
            "Auth Service",
            "Provider Service",
            "Billing Service",
            "Scheduler Service",
            "Reporting Service",
            "Search Service",
            "Gateway Service",
            "Audit Service",
            "Eligibility Service"
    };

    private static final String[] ENVIRONMENTS = {"UAT", "PROD", "DEV"};
    private static final String[] STATUSES = {"ACTIVE", "ACTIVE", "ACTIVE", "ACTIVE", "FAILED"};

    public DeploymentService() {
        generateSampleData();
    }

    private void generateSampleData() {
        LocalDateTime base = LocalDateTime.of(2026, 9, 4, 10, 30);

        for (int i = 0; i < 125; i++) {
            long id = 1001L + i;
            String name = SERVICE_NAMES[i % SERVICE_NAMES.length];
            String environment = ENVIRONMENTS[i % ENVIRONMENTS.length];

            // Keep PROD mostly healthy and introduce realistic failures.
            String status = (i % 17 == 0 || i % 29 == 0) ? "FAILED" : STATUSES[i % STATUSES.length];

            String version = "v" + (1 + (i % 4)) + "." + (i % 10) + "." + (i % 5);
            String buildNumber = "BUILD-" + (4582 - i);
            String deployedBy = environment.equals("PROD") ? "Release Manager" : "DevOps";
            LocalDateTime deployedAt = base.minusHours(i * 3L);

            deployments.add(new Deployment(
                    id,
                    name,
                    status,
                    version,
                    environment,
                    deployedAt,
                    deployedBy,
                    buildNumber
            ));
        }
    }

    public DeploymentPageResponse findAll(
            int page,
            int size,
            String search,
            String environment,
            String status
    ) {
        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), 100);

        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();
        String normalizedEnvironment = environment == null ? "" : environment.trim().toUpperCase();
        String normalizedStatus = status == null ? "" : status.trim().toUpperCase();

        List<Deployment> filtered = deployments.stream()
                .filter(d -> normalizedSearch.isBlank()
                        || d.name().toLowerCase().contains(normalizedSearch))
                .filter(d -> normalizedEnvironment.isBlank()
                        || d.environment().equalsIgnoreCase(normalizedEnvironment))
                .filter(d -> normalizedStatus.isBlank()
                        || d.status().equalsIgnoreCase(normalizedStatus))
                .sorted(Comparator.comparing(Deployment::lastDeployment).reversed())
                .toList();

        long totalRecords = filtered.size();
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());

        List<Deployment> data = filtered.subList(fromIndex, toIndex);

        return new DeploymentPageResponse(
                page,
                size,
                totalRecords,
                totalPages,
                data
        );
    }

    public SummaryResponse getSummary() {
        long total = deployments.size();
        long active = deployments.stream().filter(d -> d.status().equals("ACTIVE")).count();
        long failed = deployments.stream().filter(d -> d.status().equals("FAILED")).count();
        long uat = deployments.stream().filter(d -> d.environment().equals("UAT")).count();
        long prod = deployments.stream().filter(d -> d.environment().equals("PROD")).count();

        return new SummaryResponse(total, active, failed, uat, prod);
    }
}
