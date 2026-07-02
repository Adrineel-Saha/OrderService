package com.cognizant.orderservice.globalexceptionhandler;

import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.validation.ObjectError;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleConstraintViolation(MethodArgumentNotValidException ex) {
        String allMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(allMessages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleFeignNotFound(FeignException ex) {
        String msg = ex.getMessage();

        int lastBracket = msg.lastIndexOf('[');
        int endBracket = msg.lastIndexOf(']');
        String cleanMessage = msg.substring(lastBracket + 1, endBracket);

        return new ResponseEntity<>(cleanMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
