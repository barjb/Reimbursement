package com.barjb.application.user.validator;

import com.barjb.application.admin.AdminService;
import com.barjb.application.admin.repository.ReceiptType;
import com.barjb.application.admin.view.ReceiptData;
import com.barjb.application.user.view.ReceiptDto;
import com.barjb.application.user.view.UserRequestBodyDto;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class UserDataLimitsValidator {
    public static final int ONE = 1;
    public static final long ZERO = 0L;
    private final AdminService adminService;

    public List<IllegalArgumentException> validate(UserRequestBodyDto userRequestBodyDto) {
        var validateErrors = new ArrayList<IllegalArgumentException>();

        var limits = adminService.getLimits();

        if (isNull(userRequestBodyDto)) {
            validateErrors.add(new IllegalArgumentException("UserRequestDto is null"));
        } else {
            if (!isNull(userRequestBodyDto.getDistance())
                    && userRequestBodyDto.getDistance() > limits.getDistance()) {
                validateErrors.add(
                        new IllegalArgumentException(
                                format("Invalid distance=%s", userRequestBodyDto.getDistance())));
            }

            List<IllegalArgumentException> validateGivenReceiptData =
                    !isNull(userRequestBodyDto.getReceipt())
                            ? userRequestBodyDto.getReceipt().stream()
                            .filter(
                                    receiptData ->
                                            isNull(receiptData.getExpense())
                                                    || (isNull(receiptData.getReceiptType())
                                                    || !limits.getReceipts().stream()
                                                    .map(ReceiptData::getReceiptType)
                                                    .collect(Collectors.toList())
                                                    .contains(receiptData.getReceiptType())))
                            .map(
                                    receiptData ->
                                            new IllegalArgumentException(
                                                    String.format("Invalid receipt data: %s", receiptData)))
                            .collect(Collectors.toList())
                            : List.of();

            validateErrors.addAll(validateGivenReceiptData);

            if (nonNull(userRequestBodyDto.getReceipt()) && validateGivenReceiptData.isEmpty()) {
                Map<ReceiptType, BigDecimal> summedUpReceiptsExpensesByReceiptType =
                        getSummedUpReceiptExpensesByReceiptType(userRequestBodyDto);

                Map<ReceiptType, BigDecimal> invalidSummedUpReceiptsByType =
                        summedUpReceiptsExpensesByReceiptType.entrySet().stream()
                                .filter(
                                        entry ->
                                                limits.getReceipts().stream()
                                                        .filter(limit -> limit.getReceiptType().equals(entry.getKey()))
                                                        .map(ReceiptData::getLimit)
                                                        .allMatch(limit -> entry.getValue().compareTo(limit) == ONE))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                invalidSummedUpReceiptsByType.keySet().stream()
                        .forEach(
                                key ->
                                        validateErrors.add(
                                                new IllegalArgumentException(
                                                        format(
                                                                "Invalid sum for receipt of type %s, sum=%s",
                                                                key, summedUpReceiptsExpensesByReceiptType.get(key)))));
            }

            var allowanceDays = 0L;
            if (isNull(userRequestBodyDto.getAllowances())) {
                validateErrors.add(
                        new IllegalArgumentException(
                                format("Invalid allowances=%s", userRequestBodyDto.getAllowances())));
            } else {

                if (isNull(userRequestBodyDto.getAllowances().getStarDate())
                        || isNull(userRequestBodyDto.getAllowances().getEndDate())) {
                    validateErrors.add(
                            new IllegalArgumentException(
                                    format(
                                            "Invalid allowances startDate or endDate=%s",
                                            userRequestBodyDto.getAllowances())));
                } else {

                    long days =
                            ChronoUnit.DAYS.between(
                                    userRequestBodyDto.getAllowances().getStarDate(),
                                    userRequestBodyDto.getAllowances().getEndDate());

                    if (days < 0) {
                        validateErrors.add(
                                new IllegalArgumentException(
                                        format("StartDate is after endDate=%s", userRequestBodyDto.getAllowances())));
                    } else {
                        allowanceDays =
                                days
                                        - userRequestBodyDto.getAllowances().getExcludeDays().stream()
                                        .filter(
                                                date ->
                                                        date.isAfter(
                                                                userRequestBodyDto
                                                                        .getAllowances()
                                                                        .getStarDate()
                                                                        .minusDays(1))
                                                                && date.isBefore(
                                                                userRequestBodyDto
                                                                        .getAllowances()
                                                                        .getEndDate()
                                                                        .plusDays(1)))
                                        .count()
                                        + 1;
                    }
                }
            }

            if (!validateErrors.isEmpty()) {
                validateErrors.add(
                        new IllegalArgumentException(
                                format(
                                        "Invalid input user data, cannot calculate totalReimbursement=%s",
                                        userRequestBodyDto)));
            } else {

                BigDecimal totalDistanceExpenses =
                        limits
                                .getCarMileage()
                                .multiply(
                                        BigDecimal.valueOf(
                                                Optional.ofNullable(userRequestBodyDto.getDistance()).orElse(ZERO)));

                BigDecimal totalReceiptExpenses =
                        getSummedUpReceiptExpensesByReceiptType(userRequestBodyDto).values().stream()
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalAllowancesExpenses =
                        BigDecimal.valueOf(allowanceDays).multiply(limits.getDailyAllowance());

                var totalReimbursement =
                        totalDistanceExpenses.add(totalReceiptExpenses).add(totalAllowancesExpenses);

                if (totalReimbursement.compareTo(limits.getTotalReimbursement()) == ONE) {
                    validateErrors.add(
                            new IllegalArgumentException(
                                    format("Invalid totalReimbursement=%s", totalReimbursement)));
                }
            }
        }

        return validateErrors;
    }

    private static Map<ReceiptType, BigDecimal> getSummedUpReceiptExpensesByReceiptType(
            UserRequestBodyDto userRequestBodyDto) {
        return userRequestBodyDto.getReceipt().stream()
                .collect(
                        Collectors.groupingBy(
                                ReceiptDto::getReceiptType,
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        p -> Optional.ofNullable(p.getExpense()).orElse(BigDecimal.ZERO),
                                        (a, b) -> a.add(b))));
    }
}
