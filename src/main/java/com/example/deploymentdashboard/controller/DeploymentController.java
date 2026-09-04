package com.example.deploymentdashboard.controller;

import com.example.deploymentdashboard.model.DeploymentPageResponse;
import com.example.deploymentdashboard.model.SummaryResponse;
import com.example.deploymentdashboard.service.DeploymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deployments")
public class DeploymentController {

    private final DeploymentService deploymentService;

    public DeploymentController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @GetMapping
    public DeploymentPageResponse getDeployments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String environment,
            @RequestParam(required = false) String status
    ) {
        return deploymentService.findAll(page, size, search, environment, status);
    }

    @GetMapping("/summary")
    public SummaryResponse getSummary() {
        return deploymentService.getSummary();
    }
}
