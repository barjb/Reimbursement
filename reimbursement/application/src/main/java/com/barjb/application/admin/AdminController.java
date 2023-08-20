package com.barjb.application.admin;

import com.barjb.application.admin.validator.AdminRequestBodyValidator;
import com.barjb.application.admin.view.AdminRequestBodyDto;
import com.barjb.application.common.ExceptionHandler;
import com.barjb.application.exeception.IllegalArgumentExceptionInForm;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.sun.net.httpserver.HttpExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static com.barjb.application.common.Constants.*;
import static com.barjb.application.common.HeadersHandler.addCommonHeaders;
import static java.lang.String.format;

@RequiredArgsConstructor
@Slf4j
public class AdminController {
    public static final String VALIDATIONS_ERRORS_IN_ADMIN_FORM =
            "Validations errors in admin form:%s";
    private final AdminService adminService;
    private final ObjectMapper objectMapper;
    private final AdminRequestBodyValidator adminRequestBodyValidator;
    private final ExceptionHandler exceptionHandler;

    public void handleAdminRequest(HttpExchange exchange) {

        switch (exchange.getRequestMethod()) {
            case POST:
                try (var inputStream = exchange.getRequestBody()) {
                    var reqeustBodyString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    var requestBody = reqeustBodyString.isEmpty() ? EMPTY_BODY : reqeustBodyString;
                    objectMapper.registerModule(new JSR310Module());
                    var adminRequestDto = objectMapper.readValue(requestBody, AdminRequestBodyDto.class);

                    log.info("create admin limits, requestBody = {}", adminRequestDto);
                    var validationResult = adminRequestBodyValidator.validate(adminRequestDto);

                    if (!validationResult.isEmpty()) {
                        throw new IllegalArgumentExceptionInForm(
                                format(VALIDATIONS_ERRORS_IN_ADMIN_FORM, adminRequestDto),
                                validationResult.stream()
                                        .map(IllegalArgumentException::getMessage)
                                        .collect(Collectors.toList()));
                    }

                    var limitsView = adminService.saveAdminLimits(adminRequestDto);
                    var limitsViewAsString = objectMapper.writeValueAsString(limitsView);

                    addCommonHeaders(exchange);
                    exchange.sendResponseHeaders(OK, limitsViewAsString.getBytes().length);

                    try (var os = exchange.getResponseBody()) {
                        os.write(limitsViewAsString.getBytes());
                    }

                    log.info("Finished create admin limits, requestBody = {}", adminRequestDto);
                } catch (IllegalArgumentExceptionInForm e) {
                    exceptionHandler.handleIllegalArgumentExceptionInForm(exchange, e);
                } catch (RuntimeException re) {
                    exceptionHandler.handleRuntimeExceptionResponse(exchange, re);
                } catch (JsonMappingException jpe) {

                    exceptionHandler.handleProcessingException(exchange, jpe);

                } catch (Exception e) {

                    exceptionHandler.handleExceptionResponse(exchange, e);
                }

                break;

            case GET:
                try {
                    var limitsResponse = adminService.getLimits();

                    var limitResponseAsString = objectMapper.writeValueAsString(limitsResponse);

                    addCommonHeaders(exchange);
                    exchange.sendResponseHeaders(OK, limitResponseAsString.getBytes().length);

                    try (var os = exchange.getResponseBody()) {
                        os.write(limitResponseAsString.getBytes());
                    }

                    log.info("Finished get admin limits={}", limitsResponse);
                } catch (IllegalArgumentExceptionInForm e) {
                    exceptionHandler.handleIllegalArgumentExceptionInForm(exchange, e);
                } catch (RuntimeException re) {
                    exceptionHandler.handleRuntimeExceptionResponse(exchange, re);
                } catch (JsonMappingException jpe) {

                    exceptionHandler.handleProcessingException(exchange, jpe);

                } catch (Exception e) {

                    exceptionHandler.handleExceptionResponse(exchange, e);
                }

                break;
            case OPTIONS:
                try {
                    addCommonHeaders(exchange);
                    exchange.sendResponseHeaders(OK, -1);

                    log.info("Finished get admin OPTIONS");
                } catch (RuntimeException re) {
                    exceptionHandler.handleRuntimeExceptionResponse(exchange, re);
                } catch (Exception e) {
                    exceptionHandler.handleExceptionResponse(exchange, e);
                }
                break;
            default:
                log.info("Method not supported = {}", exchange.getRequestMethod());
        }
    }
}
