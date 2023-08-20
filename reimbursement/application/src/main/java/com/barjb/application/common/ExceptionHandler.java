package com.barjb.application.common;

import com.barjb.application.exeception.ErrorResponse;
import com.barjb.application.exeception.IllegalArgumentExceptionInForm;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static com.barjb.application.common.Constants.*;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandler {
    private final ObjectMapper objectMapper;

    public void handleIllegalArgumentExceptionInForm(
            HttpExchange exchange, IllegalArgumentExceptionInForm e) {
        log.warn("IllegalArgumentExceptionInForm error occurred", e);
        try (var responseBody = exchange.getResponseBody()) {
            exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

            var errorMessageToString =
                    objectMapper.writeValueAsString(
                            ErrorResponse.builder().message(e.getMessage()).errors(e.getErrors()).build());
            exchange.sendResponseHeaders(BAD_REQUEST, errorMessageToString.length());
            responseBody.write(errorMessageToString.getBytes());
        } catch (IOException io) {
            log.warn("IOException error occurred, can't send response", e);
        }
    }

    public void handleRuntimeExceptionResponse(HttpExchange exchange, RuntimeException re) {
        log.warn("RuntimeException error occurred", re);

        try (var responseBody = exchange.getResponseBody()) {
            exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

            var errorMessageToString =
                    objectMapper.writeValueAsString(ErrorResponse.builder().message(re.getMessage()).build());
            exchange.sendResponseHeaders(BAD_REQUEST, errorMessageToString.length());
            responseBody.write(errorMessageToString.getBytes());
        } catch (IOException e) {
            log.warn("IOException error occurred, can't send response", e);
        }
    }

    public void handleExceptionResponse(HttpExchange exchange, Exception io) {
        log.warn("Exception error occurred", io);

        try (var responseBody = exchange.getResponseBody()) {
            exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

            var errorMessageToString =
                    objectMapper.writeValueAsString(ErrorResponse.builder().message(io.getMessage()).build());
            exchange.sendResponseHeaders(SERVER_ERROR, errorMessageToString.length());
            responseBody.write(errorMessageToString.getBytes());
        } catch (IOException e) {
            log.warn("IOException error occurred, can't send response", e);
        }
    }

    public void handleProcessingException(HttpExchange exchange, JsonMappingException jpe) {
        log.warn("JsonMappingException error occurred", jpe);

        try (var responseBody = exchange.getResponseBody()) {
            exchange.getResponseHeaders().set(CONTENT_TYPE, APPLICATION_JSON);

            var errorMessageToString =
                    objectMapper.writeValueAsString(ErrorResponse.builder().message(jpe.getMessage()).build());
            exchange.sendResponseHeaders(BAD_REQUEST, errorMessageToString.length());
            responseBody.write(errorMessageToString.getBytes());
        } catch (IOException e) {
            log.warn("IOException error occurred, can't send response", e);
        }
    }
}
