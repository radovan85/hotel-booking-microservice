package com.radovan.spring.services.impl;

import com.radovan.spring.services.ConsulRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConsulRegistrationServiceImpl implements ConsulRegistrationService {

    private static final String CONSUL_REGISTRY_URL = "http://localhost:8500/v1/agent/service/register";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Scheduled(fixedRate = 30000L)
    public void registerService() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String ipAddr = InetAddress.getLocalHost().getHostAddress();

            String appName = "api-gateway";
            int port = 8090;

            Map<String, Object> registrationData = new HashMap<>();
            registrationData.put("Name", appName);
            registrationData.put("ID", appName + "-" + port); 
            registrationData.put("Address", ipAddr); 
            registrationData.put("Port", port);

            Map<String, Object> checkData = new HashMap<>();
            checkData.put("HTTP", "http://" + ipAddr + ":" + port + "/api/health"); 
            checkData.put("Interval", "10s"); 
            checkData.put("Timeout", "5s"); 

            registrationData.put("Check", checkData); 

            JsonNode jsonPayload = objectMapper.valueToTree(registrationData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<JsonNode> requestEntity = new HttpEntity<>(jsonPayload, headers);

            restTemplate.put(CONSUL_REGISTRY_URL, requestEntity);

            System.out.println("Service registered successfully with Consul!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register service with Consul", e);
        }
    }
}