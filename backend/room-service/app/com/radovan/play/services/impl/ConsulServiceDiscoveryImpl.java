package com.radovan.play.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.radovan.play.services.ConsulServiceDiscovery;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.Map;

@Singleton
public class ConsulServiceDiscoveryImpl implements ConsulServiceDiscovery {

    private static final String CONSUL_API_SERVICES_URL = "http://consul:8500/v1/agent/services";
    private final WSClient wsClient;

    @Inject
    public ConsulServiceDiscoveryImpl(WSClient wsClient) {
        this.wsClient = wsClient;
    }

    @Override
    public String getServiceUrl(String serviceName) {
        try {
            // Poziv ka Consul API-ju za dobijanje svih registrovanih servisa
            JsonNode response = wsClient.url(CONSUL_API_SERVICES_URL)
                    .get()
                    .toCompletableFuture()
                    .join()
                    .asJson();

            // Provera da li postoji odgovor
            if (response == null || response.isEmpty()) {
                throw new RuntimeException("No services found in Consul registry");
            }

            // Iteracija kroz servise i pronalaženje traženog servisa
            Iterator<Map.Entry<String, JsonNode>> fields = response.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode serviceDetails = entry.getValue();

                if (serviceName.equals(serviceDetails.get("Service").asText())) {
                    String address = serviceDetails.get("Address").asText();
                    int port = serviceDetails.get("Port").asInt();

                    if (address == null || port == 0) {
                        throw new RuntimeException("Invalid service details for: " + serviceName);
                    }

                    // Vraćanje URL-a servisa
                    return "http://" + address + ":" + port;
                }
            }

            throw new RuntimeException("Service not found: " + serviceName);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch service URL from Consul registry", e);
        }
    }
}