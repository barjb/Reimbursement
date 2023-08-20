package com.barjb.application.admin.validator;

import com.barjb.application.admin.repository.ReceiptType;
import com.barjb.application.admin.view.AdminRequestBodyDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class AdminRequestBodyValidator {

    public static final int EQUAL_MINUS_1 = -1;
    public static final String INVALID_DAILY_ALLOWANCE = "Invalid daily allowance= %s";
    public static final String INVALID_CAR_MILEAGE = "Invalid car mileage= %s";
    public static final long ZERO = 0L;
    public static final String INVALID_MAXIMUM_DISTANCE = "Invalid maximum distance= %s";
    public static final String INVALID_TOTAL_REIMBURSEMENT = "Invalid total reimbursement= %s";
    public static final String LIST_OF_RECEIPTS_CANNOT_BE_NULL_OR_EMPTY =
            "List of receipts cannot be null or empty= %s";

    public static final String INVALID_RECEIPT_DATA = "Invalid receipt data= %s";

    public List<IllegalArgumentException> validate(AdminRequestBodyDto adminRequestBodyDto) {

        List<IllegalArgumentException> exceptions = new ArrayList<>();

        if (isNull(adminRequestBodyDto.getDailyAllowance())
                || adminRequestBodyDto.getDailyAllowance().compareTo(BigDecimal.ZERO) == EQUAL_MINUS_1) {

            exceptions.add(
                    new IllegalArgumentException(
                            format(INVALID_DAILY_ALLOWANCE, adminRequestBodyDto.getDailyAllowance())));
        }
        if (isNull(adminRequestBodyDto.getCarMileage())
                || adminRequestBodyDto.getCarMileage().compareTo(BigDecimal.ZERO) == EQUAL_MINUS_1) {

            exceptions.add(
                    new IllegalArgumentException(
                            format(INVALID_CAR_MILEAGE, adminRequestBodyDto.getCarMileage())));
        }

        if (isNull(adminRequestBodyDto.getDistance()) || adminRequestBodyDto.getDistance() < ZERO) {

            exceptions.add(
                    new IllegalArgumentException(
                            format(INVALID_MAXIMUM_DISTANCE, adminRequestBodyDto.getDistance())));
        }

        if (isNull(adminRequestBodyDto.getTotalReimbursement())
                || adminRequestBodyDto.getTotalReimbursement().compareTo(BigDecimal.ZERO)
                == EQUAL_MINUS_1) {

            exceptions.add(
                    new IllegalArgumentException(
                            format(INVALID_TOTAL_REIMBURSEMENT, adminRequestBodyDto.getTotalReimbursement())));
        }

        if (isNull(adminRequestBodyDto.getReceipts()) || adminRequestBodyDto.getReceipts().isEmpty()) {
            exceptions.add(
                    new IllegalArgumentException(
                            format(
                                    LIST_OF_RECEIPTS_CANNOT_BE_NULL_OR_EMPTY,
                                    adminRequestBodyDto.getTotalReimbursement())));
        } else {

            List<IllegalArgumentException> exceptionsFromReceipts =
                    adminRequestBodyDto.getReceipts().stream()
                            .map(
                                    receiptData -> {
                                        if (isNull(receiptData.getReceiptType())
                                                || isNull(ReceiptType.fromValue(receiptData.getReceiptType().name()))
                                                || isNull(receiptData.getLimit())
                                                || receiptData.getLimit().compareTo(BigDecimal.ZERO) == EQUAL_MINUS_1
                                                || isNull(receiptData.getIsActive())) {
                                            return new IllegalArgumentException(
                                                    format(INVALID_RECEIPT_DATA, receiptData));
                                        }
                                        return null;
                                    })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

            exceptions.addAll(exceptionsFromReceipts);
        }
        return exceptions;
    }
}
