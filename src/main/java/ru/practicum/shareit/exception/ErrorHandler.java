package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final ObjectNotFoundException e) {
        return Map.of("error", "OBJECT NOT FOUND", "message", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserAlreadyExists(final UserAlreadyExist e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", " USER ALREADY EXISTS");
        errorResponse.put("message", e.getMessage());
        return errorResponse;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleItemAccessDeniedException(final ItemAccessDeniedException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "USER HAVEN'T ACCESS");
        errorResponse.put("message", e.getMessage());
        return errorResponse;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(final InvalidEntityException e) {
        return Map.of("400 BAD REQUEST", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static Map<String, String> handleInternalServerError(final Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "INTERNAL SERVER ERROR");
        errorResponse.put("message", e.getMessage());
        return errorResponse;
    }
}