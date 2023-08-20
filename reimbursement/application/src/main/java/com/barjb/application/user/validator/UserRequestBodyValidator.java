package com.barjb.application.user.validator;

import com.barjb.application.user.view.UserRequestBodyDto;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class UserRequestBodyValidator {
    private static final String INVALID_TRIP_DATE = "Invalid trip date=%s";
    private static final String INVALID_RECEIPT = "Invalid receipt=%s";
    private static final String INVALID_ALLOWANCE_S = "Invalid allowance=%s";

    private static final String INVALID_DISTANCE = "Invalid distance= %s";
    public static final int ZERO = 0;

    public List<IllegalArgumentException> validate(UserRequestBodyDto userRequestDto) {

        var exceptions = new ArrayList<IllegalArgumentException>();
        if (isNull(userRequestDto.getTripDate())) {
            exceptions.add(
                    new IllegalArgumentException(
                            format(INVALID_TRIP_DATE, userRequestDto.getTripDate())));
        }
        if (isNull(userRequestDto.getReceipt())) {
            exceptions.add(
                    new IllegalArgumentException(format(INVALID_RECEIPT, userRequestDto.getReceipt())));
        }

        if (isNull(userRequestDto.getDistance()) || userRequestDto.getDistance() < ZERO) {
            exceptions.add(
                    new IllegalArgumentException(
                            format(INVALID_DISTANCE, userRequestDto.getDistance())));
        }

        var allowance = userRequestDto.getAllowances();
        if (!isNull(allowance)) {
            if (isNull(allowance.getStarDate())
                    || isNull(allowance.getEndDate())
                    || isNull(allowance.getExcludeDays())) {
                exceptions.add(new IllegalArgumentException(format(INVALID_ALLOWANCE_S, allowance)));
            }
        }

        return exceptions;
    }
}
