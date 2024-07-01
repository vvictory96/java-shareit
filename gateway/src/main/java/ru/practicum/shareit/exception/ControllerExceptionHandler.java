package ru.practicum.shareit.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.json.ErrorResponse;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class,
            MissingRequestHeaderException.class, IllegalArgumentException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConversionFailedException(ConversionFailedException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}
