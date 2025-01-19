package edu.school21.controllers.advice;

import edu.school21.dto.ErrorInfoDto;
import edu.school21.exceptions.EmptyFileException;
import edu.school21.exceptions.InsufficientStockException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorInfoDto handleMethodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ErrorInfoDto(req.getRequestURL().toString(), errors.values().toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorInfoDto handleMethodArgumentTypeMismatchException(HttpServletRequest req, MethodArgumentTypeMismatchException e) {
        String error = e.getName() + " should be of type " + e.getRequiredType().getName();
        return new ErrorInfoDto(req.getRequestURL().toString(), error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorInfoDto handleHttpMessageNotReadableException(HttpServletRequest req, HttpMessageNotReadableException e) {
        return new ErrorInfoDto(req.getRequestURL().toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ErrorInfoDto handleMissingServletRequestPartException(HttpServletRequest req, MissingServletRequestPartException e) {
        return new ErrorInfoDto(req.getRequestURL().toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmptyFileException.class)
    public ErrorInfoDto handleEmptyFileException(HttpServletRequest req, EmptyFileException e) {
        return new ErrorInfoDto(req.getRequestURL().toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorInfoDto handleMissingServletRequestParameterException(HttpServletRequest req, MissingServletRequestParameterException e) {
        return new ErrorInfoDto(req.getRequestURL().toString(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfoDto handleDataIntegrityViolation(HttpServletRequest req, DataIntegrityViolationException e) {
        String message = e.getRootCause().getMessage().split("Подробности: ")[1];
        return new ErrorInfoDto(req.getRequestURL().toString(), message);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorInfoDto handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e) {
        StringBuilder sb = new StringBuilder();
        e.getConstraintViolations().forEach(violation -> {
            String paramName = violation.getPropertyPath().toString();
            if (paramName.contains(".")) {
                paramName = paramName.substring(paramName.lastIndexOf('.') + 1);
            }
            sb.append(paramName).append(": ").append(violation.getMessage()).append("; ");
        });
        return new ErrorInfoDto(req.getRequestURL().toString(), sb.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientStockException.class)
    public ErrorInfoDto handleInsufficientStockException(HttpServletRequest req, InsufficientStockException ex) {
        return new ErrorInfoDto(req.getRequestURL().toString(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorInfoDto handleEntityNotFoundException(HttpServletRequest req, EntityNotFoundException e) {
        return new ErrorInfoDto(req.getRequestURL().toString(), e.getMessage());
    }
}
