package com.radovan.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.radovan.spring.services.ConsulRegistrationService;

import jakarta.annotation.PostConstruct;

@Component
public class ConsulClientRegistrationInitializer {

	@Autowired
	private ConsulRegistrationService consulRegistrationService;

	@PostConstruct
	public void initialize() {
		try {
			consulRegistrationService.registerService();
		} catch (Exception e) {
			System.out.println("Error during service registration: " + e.getMessage());
			e.printStackTrace();
		}
	}
}