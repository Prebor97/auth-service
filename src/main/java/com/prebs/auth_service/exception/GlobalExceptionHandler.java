package com.prebs.auth_service.exception;

import com.prebs.auth_service.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserFoundException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleUserFoundException(UserFoundException ex, WebRequest request) {
        return new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(true)
        );
    }

    @ExceptionHandler(PasswordMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto passwordMismatchException(PasswordMismatchException ex, WebRequest request) {
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(true)
        );
    }

    @ExceptionHandler(RegistrationErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto registrationException(RegistrationErrorException ex, WebRequest request) {
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(true)
        );
    }

    @ExceptionHandler(EmailPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto loginException(EmailPasswordException ex, WebRequest request) {
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getDescription(true)
        );
    }

    @ExceptionHandler(UserNotActivatedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto activationException(UserNotActivatedException ex, WebRequest request) {
        return new ErrorResponseDto(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                request.getDescription(true)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(), // 400 Bad Request
                errorMessage,
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
