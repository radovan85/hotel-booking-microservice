package com.radovan.spring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/health")
public class HealthController {

	@GetMapping
	public ResponseEntity<String> getHealthCheck() {
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

}
