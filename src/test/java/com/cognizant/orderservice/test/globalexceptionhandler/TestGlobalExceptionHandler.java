package com.cognizant.orderservice.test.globalexceptionhandler;

import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import com.cognizant.orderservice.globalexceptionhandler.GlobalExceptionHandler;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestGlobalExceptionHandler {

    @Mock
    private ResourceNotFoundException resourceNotFoundException;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void handleResourceNotFoundException_returns404WithMessage() {
        when(resourceNotFoundException.getMessage()).thenReturn("Order not found");

        ResponseEntity<String> response = globalExceptionHandler.handleResourceNotFoundException(resourceNotFoundException);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody());
    }

    @Test
    void handleMethodArgumentNotValidException_returns400WithJoinedMessages() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        ObjectError error1 = new ObjectError("userId", "User Id must be positive");
        ObjectError error2 = new ObjectError("status", "Status must be one of: CREATED, PAID, SHIPPED, CANCELLED");
        when(bindingResult.getAllErrors()).thenReturn(List.of(error1, error2));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<String> response = globalExceptionHandler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User Id must be positive, Status must be one of: CREATED, PAID, SHIPPED, CANCELLED", response.getBody());
    }

    @Test
    void handleMethodArgumentNotValidException_singleError_returnsMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        ObjectError error = new ObjectError("userId", "User Id must be positive");
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<String> response = globalExceptionHandler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User Id must be positive", response.getBody());
    }

    @Test
    void handleFeignNotFoundException_returns404WithCleanMessage() {
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        when(feignEx.getMessage()).thenReturn("some prefix [Order not found]");

        ResponseEntity<String> response = globalExceptionHandler.handleFeignNotFound(feignEx);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody());
    }

    @Test
    void handleGenericException_returns400WithMessage() {
        Exception ex = mock(Exception.class);
        when(ex.getMessage()).thenReturn("Unexpected error");

        ResponseEntity<String> response = globalExceptionHandler.exceptionHandler(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody());
    }

    @Test
    void handleGenericException_nullMessage_returnsNullBody() {
        Exception ex = mock(Exception.class);
        when(ex.getMessage()).thenReturn(null);

        ResponseEntity<String> response = globalExceptionHandler.exceptionHandler(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}
