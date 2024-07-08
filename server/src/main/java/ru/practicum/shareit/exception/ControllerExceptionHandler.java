package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({
            ObjectExistenceException.class,
            EntityNotFoundException.class,
            ObjectAccessException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleObjectExistenceException(final Exception e) {
        return Map.of("", e.getMessage());
    }

    @ExceptionHandler({
            ObjectAvailabilityException.class,
            ObjectCreationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleObjectAvailabilityException(final Exception e) {
        return Map.of("", e.getMessage());
    }
}
