package com.cognizant.orderservice.main;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cognizant.orderservice.*")
@EnableJpaRepositories(basePackages = "com.cognizant.orderservice.repositories")
@EnableFeignClients(basePackages = "com.cognizant.orderservice.feignclients")
@EntityScan(basePackages = "com.cognizant.orderservice.entities")
@EnableDiscoveryClient(autoRegister=true)
@OpenAPIDefinition(
		info=@Info(
				title="Order Service REST API Documentation",
				description="REST API Documentation for Order Service",
				version="v1.0"
		)
)
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper()
	{
		return new ModelMapper();
	}
}
