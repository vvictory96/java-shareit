package ru.practicum.shareit.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.json.ErrorResponse;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({ObjectExistenceException.class,})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectExistenceException(final ObjectExistenceException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ObjectAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectAccessException(final ObjectAccessException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({ObjectCreationException.class, ObjectAvailabilityException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleObjectCreatingException(final Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({ObjectUpdateException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleObjectUpdateException(final ObjectUpdateException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConversionFailedException(ConversionFailedException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentException(ArgumentException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}
