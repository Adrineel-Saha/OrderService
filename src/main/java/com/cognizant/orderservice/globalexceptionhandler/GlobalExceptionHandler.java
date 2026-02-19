package com.cognizant.orderservice.globalexceptionhandler;

import com.cognizant.orderservice.exceptions.ResourceNotFoundException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex){
        ResponseEntity<String> errorResponse = new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleConstraintViolation(MethodArgumentNotValidException ex) {
        String allMessages = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> err.getDefaultMessage())   // only the human-readable message
                .collect(Collectors.joining(", "));

        ResponseEntity<String> errorResponse = new ResponseEntity<String>(allMessages, HttpStatus.BAD_REQUEST);
        return errorResponse;
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<String> handleFeignNotFound(FeignException ex) {
        String msg = ex.getMessage();

        int lastBracket = msg.lastIndexOf('[');
        int endBracket = msg.lastIndexOf(']');
        String cleanMessage = msg.substring(lastBracket + 1, endBracket);

        ResponseEntity<String> errorResponse = new ResponseEntity<String>(cleanMessage, HttpStatus.NOT_FOUND);
        return errorResponse;
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        ResponseEntity<String> errorResponse = new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        return errorResponse;
    }
}
