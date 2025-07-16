package com.radovan.spring.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.services.ConsulServiceDiscovery;

@Component
public class ServiceUrlProvider {

	private final Map<String, String> cachedServiceUrls = new ConcurrentHashMap<>();
	private ConsulServiceDiscovery consulServiceDiscovery;

	@Autowired
	private void initialize(ConsulServiceDiscovery consulServiceDiscovery) {
		this.consulServiceDiscovery = consulServiceDiscovery;
	}

	public String getServiceUrl(String serviceName) {
		return cachedServiceUrls.computeIfAbsent(serviceName, key -> {
			try {
				String serviceUrl = consulServiceDiscovery.getServiceUrl(serviceName);
				validateUrl(serviceUrl, serviceName);
				return serviceUrl;
			} catch (RuntimeException e) {
				System.err.println("Failed to retrieve service URL for: " + serviceName + " - " + e.getMessage());
				throw e;
			}
		});
	}

	public String getAuthServiceUrl() {
		return getServiceUrl("auth-service");
	}

	public String getGuestServiceUrl() {
		return getServiceUrl("guest-service");
	}

	public String getReservationServiceUrl() {
		return getServiceUrl("reservation-service");
	}

	public String getRoomServiceUrl() {
		return getServiceUrl("room-service");
	}

	private void validateUrl(String url, String serviceName) {
		if (url == null || !url.startsWith("http")) {
			throw new IllegalArgumentException("Invalid URL for " + serviceName + ": " + url);
		}
	}

}
