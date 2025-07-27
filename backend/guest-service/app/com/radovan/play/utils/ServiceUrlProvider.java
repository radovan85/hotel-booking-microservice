package com.radovan.play.utils;

import com.radovan.play.services.ConsulServiceDiscovery;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ServiceUrlProvider {

    private final Map<String, String> cachedServiceUrls = new ConcurrentHashMap<>();
    private final ConsulServiceDiscovery consulServiceDiscovery;

    @Inject
    public ServiceUrlProvider(ConsulServiceDiscovery consulServiceDiscovery) {
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

    private void validateUrl(String url, String serviceName) {
        if (url == null || !url.startsWith("http")) {
            throw new IllegalArgumentException("Invalid URL for " + serviceName + ": " + url);
        }
    }


}