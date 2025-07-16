package com.radovan.spring.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.radovan.spring.services.ConsulRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConsulRegistrationServiceImpl implements ConsulRegistrationService {

	private static final String CONSUL_REGISTRY_URL = "http://consul:8500/v1/agent/service/register";

	@Autowired
	private RestTemplate restTemplate;

	@Override
	@Scheduled(fixedRate = 30000L)
	public void registerService() {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			// Dinamičko dohvaćanje IP adrese i hostname-a
			// String hostname = InetAddress.getLocalHost().getHostName();
			String ipAddr = InetAddress.getLocalHost().getHostAddress();

			String appName = "auth-service";
			int port = 8080;

			// Kreiranje podataka za registraciju za Consul
			Map<String, Object> registrationData = new HashMap<>();
			registrationData.put("Name", appName); // Ime servisa
			registrationData.put("ID", appName + "-" + port); // Jedinstveni ID
			registrationData.put("Address", ipAddr); // IP adresa
			registrationData.put("Port", port);

			// Health-check konfiguracija
			Map<String, Object> checkData = new HashMap<>();
			checkData.put("HTTP", "http://" + ipAddr + ":" + port + "/api/health"); // Health-check URL
			checkData.put("Interval", "10s"); // Interval health-checka
			checkData.put("Timeout", "5s"); // Timeout za health-check

			registrationData.put("Check", checkData); // Dodavanje health-checka

			// Konverzija u JSONNode
			JsonNode jsonPayload = objectMapper.valueToTree(registrationData);

			// Postavljanje Content-Type zaglavlja
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			// Kreiranje HttpEntity sa JSON telom i zaglavljem
			HttpEntity<JsonNode> requestEntity = new HttpEntity<>(jsonPayload, headers);

			// Slanje POST zahteva ka Consul registry API-ju
			restTemplate.put(CONSUL_REGISTRY_URL, requestEntity);

			System.out.println("Service registered successfully with Consul!");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to register service with Consul", e);
		}
	}
}