package com.digitalmoneyhouse.iamservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> argumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        Map<String, String> extra = new HashMap<>();
        for (FieldError error : fieldErrors) {
            extra.put(error.getField() , error.getDefaultMessage());
        }
        ErrorResponse response = new ErrorResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("One or more fields are invalid");
        response.setExtra(extra);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> messageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        String message = ex.getMessage();
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErrorResponse> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        String method = ex.getMethod();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(String.format("%s method is not allowed at this path", method));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> dataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        String message = ex.getMostSpecificCause().getMessage();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage("An unexpected error has occurred");
        if (message.contains("Duplicate entry")) {
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String duplicatedValue = matcher.group(1);
                errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                errorResponse.setMessage(String.format("Data '%s' already in use", duplicatedValue));
            }
        }
        if (message.contains("Data too long for column")) {
            Pattern pattern = Pattern.compile("column '(.*?)'");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                String[] columnNameParts = columnName.split("_");
                StringBuilder camelCase = new StringBuilder();
                camelCase.append(columnNameParts[0]);
                for (int i=1; i < columnNameParts.length; i++) {
                    camelCase.append(Character.toUpperCase(columnNameParts[i].charAt(0)));
                    camelCase.append(columnNameParts[i].substring(1));
                }
                errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                errorResponse.setMessage(String.format("Data too long for field %s", camelCase));
            }
        }
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
