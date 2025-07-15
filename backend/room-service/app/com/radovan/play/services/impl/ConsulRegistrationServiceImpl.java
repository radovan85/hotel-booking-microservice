package com.radovan.play.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.play.services.ConsulRegistrationService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ConsulRegistrationServiceImpl implements ConsulRegistrationService {

    private static final String CONSUL_SERVER_URL = "http://localhost:8500/v1/agent/service/register"; // Consul endpoint
    private final WSClient wsClient;
    private final ObjectMapper objectMapper;

    @Inject
    public ConsulRegistrationServiceImpl(WSClient wsClient, ObjectMapper objectMapper) {
        this.wsClient = wsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public void registerService() {
        try {

            // Dynamically fetch hostname and IP address
            String hostname = InetAddress.getLocalHost().getHostName();
            String ipAddr = InetAddress.getLocalHost().getHostAddress();


            String appName = "room-service";
            int port = 9002;

            // Create registration data
            Map<String, Object> serviceData = new HashMap<>();
            serviceData.put("ID", appName + "-" + port); // Unique service ID
            serviceData.put("Name", appName); // Service name
            serviceData.put("Address", ipAddr); // Service IP
            serviceData.put("Port", port); // Service port

            // Define health check
            Map<String, String> check = new HashMap<>();
            check.put("HTTP", "http://" + ipAddr + ":" + port + "/api/health");
            check.put("Interval", "10s");
            check.put("Timeout", "1s");
            serviceData.put("Check", check);

            System.out.println("Service data: " + serviceData);

            // Convert to JSONNode
            JsonNode jsonPayload = objectMapper.valueToTree(serviceData);

            // Send PUT request to Consul

            wsClient.url(CONSUL_SERVER_URL)
                    .addHeader("Content-Type", "application/json")
                    .setMethod("PUT") // Postavite HTTP metod na PUT
                    .setBody(jsonPayload) // Dodajte telo zahteva
                    .execute() // Pošaljite zahtev
                    .thenAccept(this::handleResponse)
                    .toCompletableFuture()
                    .join(); // Osigurajte sinkronizovanu egzekuciju

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register service with Consul", e);
        }
    }

    private void handleResponse(WSResponse response) {
        if (response.getStatus() == 200 || response.getStatus() == 204) {
            System.out.println("Service registered successfully with Consul!");
        } else {
            System.err.println("Failed to register service with Consul: " + response.getStatusText());
            System.err.println("Response body: " + response.getBody());
        }
    }
}
