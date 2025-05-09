package com.siemens.internship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
public class InternshipApplication {

	@Bean(name = "customTaskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5); // Number of core threads
		executor.setMaxPoolSize(10); // Maximum number of threads
		executor.setQueueCapacity(25); // Queue capacity
		executor.setThreadNamePrefix("AsyncExecutor-");
		executor.initialize();
		return executor;
	}

	public static void main(String[] args) {
		SpringApplication.run(InternshipApplication.class, args);
	}
}
