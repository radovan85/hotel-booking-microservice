package com.radovan.spring.services.impl;

import java.util.Map;
import java.util.Optional;

import com.radovan.spring.services.ConsulServiceDiscovery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsulServiceDiscoveryImpl implements ConsulServiceDiscovery {

	private static final String CONSUL_API_SERVICES_URL = "http://localhost:8500/v1/agent/services";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String getServiceUrl(String serviceName) {
		try {
			// Poziv ka Consul API-ju za dobijanje svih registrovanih servisa
			ResponseEntity<Map<String, Map<String, Object>>> response = restTemplate.getForEntity(
					CONSUL_API_SERVICES_URL, (Class<Map<String, Map<String, Object>>>) (Object) Map.class);

			// Provera da li postoji odgovor
			Map<String, Map<String, Object>> services = response.getBody();
			if (services == null || services.isEmpty()) {
				throw new RuntimeException("No services found in Consul registry");
			}

			// Pretraga servisa po imenu
			Optional<Map.Entry<String, Map<String, Object>>> serviceEntry = services.entrySet().stream()
					.filter(entry -> serviceName.equals(entry.getValue().get("Service"))) // Consul koristi ključ
																							// "Service" za ime servisa
					.findFirst();

			if (serviceEntry.isEmpty()) {
				throw new RuntimeException("Service not found: " + serviceName);
			}

			// Dohvatanje adrese i porta registrovanog servisa
			Map<String, Object> serviceDetails = serviceEntry.get().getValue();
			String address = (String) serviceDetails.get("Address");
			Integer port = (Integer) serviceDetails.get("Port");

			if (address == null || port == null) {
				throw new RuntimeException("Invalid service details for: " + serviceName);
			}

			// Vraćanje URL-a servisa
			return "http://" + address + ":" + port;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch service URL from Consul registry", e);
		}
	}
}