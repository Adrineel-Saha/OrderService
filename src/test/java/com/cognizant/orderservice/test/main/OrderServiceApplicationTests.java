package com.cognizant.orderservice.test.main;

import com.cognizant.orderservice.controllers.OrderServiceController;
import com.cognizant.orderservice.main.OrderServiceApplication;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OrderServiceApplication.class)
@ActiveProfiles("test")
class OrderServiceApplicationTests {

    @Autowired
    private OrderServiceController orderServiceController;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OpenAPI openAPI;

    @Test
    void contextLoads() {
        assertNotNull(orderServiceController);
    }

    @Test
    void modelMapperBean_isNotNull() {
        assertNotNull(modelMapper);
        assertInstanceOf(ModelMapper.class, modelMapper);
    }

    @Test
    void customOpenAPI_hasExpectedServer() {
        assertNotNull(openAPI);
        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
        assertEquals("http://localhost:9191", openAPI.getServers().get(0).getUrl());
        assertEquals("API Gateway", openAPI.getServers().get(0).getDescription());
    }

    @Test
    void customOpenAPI_hasBearerAuthSecurityScheme() {
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("bearerAuth"));

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
    }

    @Test
    void customOpenAPI_hasSecurityRequirement() {
        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        SecurityRequirement req = openAPI.getSecurity().get(0);
        assertTrue(req.containsKey("bearerAuth"));
    }

    @Test
    void main_doesNotThrow() {
        assertDoesNotThrow(() ->
                OrderServiceApplication.main(new String[]{"--spring.profiles.active=test",
                        "--spring.main.web-application-type=none"})
        );
    }
}
