package com.siemens.internship;

import com.siemens.internship.controller.ItemController;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InternshipApplicationTests {

	@Autowired
	private ItemController itemController;

	@Autowired
	private ItemService itemService;


	@Test
	void testItemControllerBeanLoaded() {
		// Verifies that the ItemController bean is loaded into the context
		assertThat(itemController).isNotNull();
	}

	@Test
	void testItemServiceBeanLoaded() {
		// Verifies that the ItemService bean is loaded into the context
		assertThat(itemService).isNotNull();
	}
}