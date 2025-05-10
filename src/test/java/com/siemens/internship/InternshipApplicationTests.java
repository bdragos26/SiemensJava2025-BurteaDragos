package com.siemens.internship;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InternshipApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void testItemControllerBeanLoaded() {
		assertThat(applicationContext.getBean("itemController")).isNotNull();
	}

	@Test
	void testItemServiceBeanLoaded() {
		assertThat(applicationContext.getBean("itemService")).isNotNull();
	}

	@Test
	void testCustomTaskExecutorBeanLoaded() {
		Executor executor = (Executor) applicationContext.getBean("customTaskExecutor");
		assertThat(executor).isNotNull();
	}

	@Test
	void testApplicationContextLoads() {
		assertThat(applicationContext).isNotNull();
	}
}