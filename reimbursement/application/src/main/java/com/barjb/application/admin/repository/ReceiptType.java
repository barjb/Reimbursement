package com.barjb.application.admin.repository;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ReceiptType {
    TAXI("TAXI"),
    HOTEL("HOTEL"),
    PLANE("PLANE"),
    TRAIN("TRAIN");

    public static final String CANNOT_DESERIALIZE_GIVEN_ENUM_STRING =
            "Cannot deserialize given enum string=%s";
    private final String value;

    @JsonCreator
    public static ReceiptType fromValue(String valueToDeserialize) {
        return Stream.of(values())
                .filter(receiptType -> receiptType.value.equalsIgnoreCase(valueToDeserialize))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        String.format(CANNOT_DESERIALIZE_GIVEN_ENUM_STRING, valueToDeserialize)));
    }
}
