package com.radovan.spring.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.radovan.spring.services.ConsulServiceDiscovery;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

import java.net.URI;
import java.util.*;

@Service
public class ConsulServiceDiscoveryImpl implements ConsulServiceDiscovery {

    private static final String CONSUL_BASE_URL = "http://consul:8500";
    //private static final String SERVICE_CATALOG_URL = CONSUL_BASE_URL + "/v1/catalog/service/%s";
    private static final String SERVICE_HEALTH_URL = CONSUL_BASE_URL + "/v1/health/service/%s?passing";
    private static final String SERVICES_LIST_URL = CONSUL_BASE_URL + "/v1/catalog/services";

    private RestTemplate restTemplate;
    
    
    @Autowired
    private void initialize(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
    public String getServiceUrl(String serviceName) {
		logAvailableServices();
        try {
            // Preferiraj samo zdrave instance
            String healthUrl = String.format(SERVICE_HEALTH_URL, serviceName);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                URI.create(healthUrl),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> serviceEntries = response.getBody();

            if (serviceEntries == null || serviceEntries.isEmpty()) {
                throw new RuntimeException("No healthy instances found for service: " + serviceName);
            }

            Map<String, Object> service = (Map<String, Object>) serviceEntries.get(0).get("Service");

            String address = (String) service.get("Address");
            Integer port = (Integer) service.get("Port");

            if (address == null || port == null) {
                throw new RuntimeException("Missing address or port for service: " + serviceName);
            }

            return String.format("http://%s:%d", address, port);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to fetch service URL from Consul registry", ex);
        }
    }

    /**
     * Optional debug metoda: štampa sve dostupne servise iz Catalog-a
     */
    public void logAvailableServices() {
        try {
            ResponseEntity<Map<String, List<String>>> response = restTemplate.exchange(
                URI.create(SERVICES_LIST_URL),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
            );

            Map<String, List<String>> services = response.getBody();

            System.out.println("=== Available Consul Services ===");
            services.forEach((name, tags) -> {
                System.out.println("- " + name + " [tags: " + String.join(", ", tags) + "]");
            });
        } catch (Exception e) {
            System.err.println("Failed to fetch services list from Consul");
            e.printStackTrace();
        }
    }
}
