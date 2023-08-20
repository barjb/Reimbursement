package com.barjb.application;

import com.barjb.application.admin.AdminController;
import com.barjb.application.admin.AdminService;
import com.barjb.application.admin.repository.LimitsDaoImpl;
import com.barjb.application.admin.validator.AdminRequestBodyValidator;
import com.barjb.application.common.ExceptionHandler;
import com.barjb.application.user.UserController;
import com.barjb.application.user.UserService;
import com.barjb.application.user.repository.ReimbursementDaoInMemoryImpl;
import com.barjb.application.user.validator.UserDataLimitsValidator;
import com.barjb.application.user.validator.UserRequestBodyValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

public class App {

    private static final String CANNOT_BIND_TO_REQUESTED_ADDRESS =
            "Cannot bind to requested address: ";

    private static final String HTTP_SERVER_INITIALIZATION_ERROR =
            "Http server initialization error: ";
    private final AdminController adminController;
    private final UserController userController;
    private final HttpServer httpServer;

    public App(
            AdminController adminController, UserController userController, HttpServer httpServer) {
        this.adminController = adminController;
        this.userController = userController;
        this.httpServer = httpServer;
    }

    public static void main(String[] args) {

        var objectMapper = new ObjectMapper();
        var limitsDaoImpl = new LimitsDaoImpl(objectMapper);
        var adminService = new AdminService(limitsDaoImpl);
        var userService =
                new UserService(new ReimbursementDaoInMemoryImpl(objectMapper), limitsDaoImpl);
        var exceptionHandler = new ExceptionHandler(objectMapper);

        try {

            var app =
                    new App(
                            new AdminController(
                                    adminService, objectMapper, new AdminRequestBodyValidator(), exceptionHandler),
                            new UserController(
                                    userService,
                                    objectMapper,
                                    new UserRequestBodyValidator(),
                                    new UserDataLimitsValidator(adminService),
                                    exceptionHandler),
                            HttpServer.create(new InetSocketAddress(8500), 0));

            app.bindUserEndpoints();
            app.bindAdminEndpoints();
            app.startApplication();
        } catch (BindException e) {
            throw new RuntimeException(CANNOT_BIND_TO_REQUESTED_ADDRESS, e);
        } catch (IOException e) {
            throw new RuntimeException(HTTP_SERVER_INITIALIZATION_ERROR, e);
        }
    }

    private void bindUserEndpoints() {
        httpServer.createContext("/user", userController::handleUserRequest);
    }

    private void bindAdminEndpoints() {
        httpServer.createContext("/admin", adminController::handleAdminRequest);
    }

    private void startApplication() {
        httpServer.start();
    }
}
