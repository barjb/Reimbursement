package com.barjb.application.exeception;

import lombok.Getter;

import java.util.List;

@Getter
public class IllegalArgumentExceptionInForm extends RuntimeException {
    String message;
    List<Object> errors;

    public IllegalArgumentExceptionInForm(String message) {
        this.message = message;
    }

    public IllegalArgumentExceptionInForm(String message, List<Object> errors) {
        this.message = message;
        this.errors = errors;
    }
}
