package com.eventlinkr.userservice.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import com.eventlinkr.userservice.domain.dto.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Global exception handler caught: {}", ex.getMessage(), ex);

        ApiError apiError;
        HttpStatus status;

        if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            apiError = new ApiError(status.value(), "Resource not found", ex.getMessage());
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            apiError = new ApiError(status.value(), "Invalid request", ex.getMessage());
        } else if (ex instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
            apiError = new ApiError(status.value(), "Authentication required", ex.getMessage());
        } else if (ex instanceof ForbiddenException) {
            status = HttpStatus.FORBIDDEN;
            apiError = new ApiError(status.value(), "Access denied", ex.getMessage());
        } else if (ex instanceof ValidationException) {
            status = HttpStatus.BAD_REQUEST;
            apiError = new ApiError(status.value(), "Validation error", ex.getMessage());

            if (ex instanceof ValidationException validationEx && validationEx.getErrors() != null) {
                validationEx.getErrors().forEach(apiError::addError);
            }
        } else if (ex instanceof WebExchangeBindException bindException) {
            status = HttpStatus.BAD_REQUEST;
            apiError = new ApiError(status.value(), "Validation error", "Request validation failed");

            bindException.getBindingResult().getFieldErrors().forEach(
                    fieldError -> apiError.addError(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        } else if (ex instanceof ServerWebInputException) {
            status = HttpStatus.BAD_REQUEST;
            apiError = new ApiError(status.value(), "Invalid input", ex.getMessage());
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            apiError = new ApiError(status.value(), "Request error", responseStatusException.getReason());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            apiError = new ApiError(status.value(), "Internal server error", "An unexpected error occurred");
        }

        // Add request path to the error response
        apiError.setPath(exchange.getRequest().getPath().value());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer dataBuffer;
        try {
            dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(apiError));
        } catch (JsonProcessingException e) {
            log.error("Error writing response", e);
            dataBuffer = bufferFactory.wrap("".getBytes());
        }

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}
