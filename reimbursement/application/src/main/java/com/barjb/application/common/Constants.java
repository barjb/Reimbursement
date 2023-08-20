package com.barjb.application.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String EMPTY_BODY = "{}";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String OPTIONS = "OPTIONS";
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int SERVER_ERROR = 500;

    public static final String DASH = "-";
    public static final String EMPTY = "";
    public static final String INVALID_PROCESSING_DURING_DEEP_COPY =
            "Invalid processing during deep copy: %s";
}
