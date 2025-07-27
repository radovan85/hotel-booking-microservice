package com.radovan.spring.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.radovan.spring.services.ApiGatewayService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/**")
public class ApiGatewayController {

	@Autowired
	private ApiGatewayService apiGatewayService;

	@RequestMapping
	public ResponseEntity<String> proxyRequest(HttpServletRequest request)
			throws JsonMappingException, JsonProcessingException {
		String requestUri = request.getRequestURI();
		String[] pathSegments = requestUri.split("/");

		if (pathSegments.length < 3) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid API request format");
		}

		if ("guests".equalsIgnoreCase(pathSegments[2]) && pathSegments.length > 3
				&& "register".equalsIgnoreCase(pathSegments[3])) {
			return apiGatewayService.forwardRequest("guest-service", request);
		}

		String firstSegment = pathSegments[2];

		String serviceName = mapSegmentToService(firstSegment);
		if (serviceName == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unknown service for segment: " + firstSegment);
		}

		return apiGatewayService.forwardRequest(serviceName, request);

	}

	private String mapSegmentToService(String segment) {
		Map<String, String> serviceMappings = Map.of("rooms", "room-service", "categories", "room-service", "auth",
				"auth-service", "guests", "guest-service", "reservations", "reservation-service", "notes",
				"reservation-service");

		return serviceMappings.get(segment);
	}
}