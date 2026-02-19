package com.cognizant.orderservice.test.main;

import com.cognizant.orderservice.controllers.OrderServiceController;
import com.cognizant.orderservice.main.OrderServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes= OrderServiceApplication.class)
@ActiveProfiles("test")
class OrderServiceApplicationTests {
	@Autowired
	private OrderServiceController orderServiceController;

	@Test
	void contextLoads() {
		assertNotNull(orderServiceController);
	}

}
