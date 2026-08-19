package com.example.springbooteksapi.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public informational endpoints used by the deployment pipeline and clients.
 */
@RestController
@RequestMapping("/api")
public class InfoController {

    /**
     * Returns basic service information; consumed by the verify stage health check.
     *
     * @return service name, version indicator and server timestamp
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        return ResponseEntity.ok(Map.of(
            "service", "springboot-eks-api",
            "status", "UP",
            "timestamp", Instant.now().toString()
        ));
    }
}
