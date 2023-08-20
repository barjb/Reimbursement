package com.barjb.application.admin.validator;

import com.barjb.application.admin.repository.ReceiptType;
import com.barjb.application.admin.view.AdminRequestBodyDto;
import com.barjb.application.admin.view.ReceiptData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

class AdminRequestBodyValidatorTest {

    private AdminRequestBodyValidator adminRequestBodyValidator = new AdminRequestBodyValidator();

    @ParameterizedTest
    @MethodSource("providesData")
    @DisplayName("Should return list of exceptions")
    void shouldThrowGivenNumberOfExceptions(AdminRequestBodyDto givenAdminRequestDto, int errorSize) {

        // when
        var validateErrors = adminRequestBodyValidator.validate(givenAdminRequestDto);
        // then
        assertThat(validateErrors).hasSize(errorSize);
    }

    @Test
    @DisplayName("Should return no exceptions when data ara valid")
    void shouldReturnNoExceptionsWhenDataAreValid() {

        // given
        var adminRequestBodyDto =
                AdminRequestBodyDto.builder()
                        .dailyAllowance(BigDecimal.valueOf(15))
                        .distance(2L)
                        .carMileage(BigDecimal.valueOf(0.3))
                        .totalReimbursement(BigDecimal.valueOf(10000))
                        .receipts(
                                List.of(
                                        ReceiptData.builder()
                                                .receiptType(ReceiptType.PLANE)
                                                .isActive(TRUE)
                                                .limit(BigDecimal.valueOf(100))
                                                .build()))
                        .build();

        // when
        var validateErrors = adminRequestBodyValidator.validate(adminRequestBodyDto);
        // then
        assertThat(validateErrors).hasSize(0);
    }

    private static Stream<Arguments> providesData() {
        return Stream.of(
                Arguments.of(AdminRequestBodyDto.builder().build(), 5),
                Arguments.of(
                        AdminRequestBodyDto.builder()
                                .dailyAllowance(BigDecimal.valueOf(15))
                                .distance(2L)
                                .carMileage(BigDecimal.valueOf(0.3))
                                .build(),
                        2),
                Arguments.of(
                        AdminRequestBodyDto.builder()
                                .dailyAllowance(BigDecimal.valueOf(15))
                                .carMileage(BigDecimal.valueOf(0.3))
                                .totalReimbursement(BigDecimal.valueOf(10000))
                                .receipts(
                                        List.of(
                                                ReceiptData.builder()
                                                        .receiptType(ReceiptType.PLANE)
                                                        .isActive(TRUE)
                                                        .limit(BigDecimal.valueOf(100))
                                                        .build()))
                                .build(),
                        1));
    }
}
