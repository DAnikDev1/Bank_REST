package com.example.bankcards.exception.handler;

import com.example.bankcards.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse processEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(EncryptionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse processEncryptionException(EncryptionException e) {
        log.error("Encryption exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(ExistedEntityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse processExistedEntityException(ExistedEntityException e) {
        log.error("Existed entity exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(CardLimitExceededException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse processUnprocessableEntityException(CardLimitExceededException e) {
        log.error("Unprocessable entity exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse processBadRequestException(DataValidationException e) {
        log.error("Data validation exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(NoAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse processBadRequestException(NoAccessException e) {
        log.error("No access exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse processOtherException(Exception e) {
        log.error("Other exception: {}", e.getMessage());
        return ErrorResponse.builder().message(e.getMessage()).status(500).build();
    }

}
