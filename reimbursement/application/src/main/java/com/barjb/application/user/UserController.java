package com.barjb.application.user;

import com.barjb.application.common.ExceptionHandler;
import com.barjb.application.exeception.IllegalArgumentExceptionInForm;
import com.barjb.application.user.validator.UserDataLimitsValidator;
import com.barjb.application.user.validator.UserRequestBodyValidator;
import com.barjb.application.user.view.UserRequestBodyDto;
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
public class UserController {

    public static final String VALIDATION_EXCEPTIONS_IN_USER_FORM =
            "Validation exceptions in user form=%s";
    public static final String VALIDATION_EXCEPTIONS_IN_USER_FORM_EXPECTED_LIMITS =
            "Validation exceptions in user form (expected limits)=%s";
    private final UserService userService;
    private final ObjectMapper objectMapper;

    private final UserRequestBodyValidator userRequestBodyValidator;

    private final UserDataLimitsValidator userDataLimitsValidator;

    private final ExceptionHandler exceptionHandler;

    public void handleUserRequest(HttpExchange exchange) {

        switch (exchange.getRequestMethod()) {
            case POST:
                try (var inputStream = exchange.getRequestBody()) {
                    var reqeustBodyString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    var requestBody = reqeustBodyString.isEmpty() ? EMPTY_BODY : reqeustBodyString;
                    objectMapper.registerModule(new JSR310Module());
                    var userRequestDto = objectMapper.readValue(requestBody, UserRequestBodyDto.class);

                    log.info("create user reimbursement, requestBody = {}", userRequestDto);
                    var validateForData = userRequestBodyValidator.validate(userRequestDto);

                    if (!validateForData.isEmpty()) {
                        throw new IllegalArgumentExceptionInForm(
                                format(VALIDATION_EXCEPTIONS_IN_USER_FORM, userRequestDto),
                                validateForData.stream()
                                        .map(IllegalArgumentException::getMessage)
                                        .collect(Collectors.toList()));
                    }

                    var validateLimitsForFormData = userDataLimitsValidator.validate(userRequestDto);

                    if (!validateLimitsForFormData.isEmpty()) {
                        throw new IllegalArgumentExceptionInForm(
                                format(VALIDATION_EXCEPTIONS_IN_USER_FORM_EXPECTED_LIMITS, userRequestDto),
                                validateLimitsForFormData.stream()
                                        .map(IllegalArgumentException::getMessage)
                                        .collect(Collectors.toList()));
                    }

                    var reimbursementView = userService.saveReimbursement(userRequestDto);

                    var reimbursementAsString = objectMapper.writeValueAsString(reimbursementView);

                    addCommonHeaders(exchange);
                    exchange.sendResponseHeaders(OK, reimbursementAsString.getBytes().length);
                    try (var os = exchange.getResponseBody()) {
                        os.write(reimbursementAsString.getBytes());
                    }

                    log.info("Finished create user reimbursement, requestBody = {}", userRequestDto);

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
                    var limitsUserResponse = userService.getLimitsViewUser();

                    var limitUserResponseAsString = objectMapper.writeValueAsString(limitsUserResponse);

                    addCommonHeaders(exchange);
                    exchange.sendResponseHeaders(OK, limitUserResponseAsString.getBytes().length);

                    try (var os = exchange.getResponseBody()) {
                        os.write(limitUserResponseAsString.getBytes());
                    }
                    log.info("Finished get user limits={}", limitsUserResponse);

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
                    log.info("Finished get user OPTIONS");
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
