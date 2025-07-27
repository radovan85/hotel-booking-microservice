package com.radovan.spring.services;

public interface PrometheusService {

	void increaseRequestCount();

	void recordResponseTime(double duration);

	void updateMemoryUsage();

	void updateThreadCount();

	void updateCpuLoad();

	void updateDatabaseQueryCount();

	void updateHeapAllocationRate();

	void updateActiveSessions();

	void updateHttpStatusCount(int statusCode);

	void updateExternalApiLatency(double duration);
}