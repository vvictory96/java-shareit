package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.json.ErrorResponse;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({ObjectExistenceException.class,})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectExistenceException(final ObjectExistenceException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ObjectAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleObjectAccessException(final ObjectAccessException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({ObjectCreationException.class,})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleObjectCreatingException(final ObjectCreationException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({ObjectUpdateException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleObjectUpdateException(final ObjectUpdateException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}
