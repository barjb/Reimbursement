package com.barjb.application.user.validator;

import com.barjb.application.admin.AdminService;
import com.barjb.application.admin.repository.ReceiptType;
import com.barjb.application.admin.view.AdminResponseBodyDto;
import com.barjb.application.admin.view.ReceiptData;
import com.barjb.application.user.view.DailyAllowance;
import com.barjb.application.user.view.ReceiptDto;
import com.barjb.application.user.view.UserRequestBodyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDataLimitsValidatorTest {
    @Mock
    private AdminService adminService;
    @InjectMocks
    private UserDataLimitsValidator userDataLimitsValidator;

    @BeforeEach
    void init() {
        givenLimits();
    }

    @Test
    @DisplayName("Should return return no exceptions")
    void shouldReturnNoExceptions() {
        // given
        var givenRequestBody =
                UserRequestBodyDto.builder()
                        .tripDate(LocalDate.of(2023, 8, 8))
                        .distance(99L)
                        .allowances(
                                DailyAllowance.builder()
                                        .starDate(LocalDate.of(2023, 8, 8))
                                        .endDate(LocalDate.of(2023, 8, 10))
                                        .excludeDays(List.of(LocalDate.of(2023, 8, 9)))
                                        .build())
                        .receipt(
                                List.of(
                                        ReceiptDto.builder()
                                                .receiptType(ReceiptType.TRAIN)
                                                .expense(BigDecimal.valueOf(10))
                                                .build()))
                        .build();

        // when
        var validateErrors = userDataLimitsValidator.validate(givenRequestBody);

        // then
        assertThat(validateErrors).hasSize(0);
    }

    @ParameterizedTest
    @MethodSource("providesData")
    @DisplayName("Should return given number of exceptions")
    void shouldReturnGivenNumberOfExceptions(
            UserRequestBodyDto givenUserRequestBodyDto, int errorSize) {
        // given
        // when
        var validateErrors = userDataLimitsValidator.validate(givenUserRequestBodyDto);

        // then
        assertThat(validateErrors.size()).isEqualTo(errorSize);
    }

    private static Stream<Arguments> providesData() {
        return Stream.of(
                Arguments.of(
                        UserRequestBodyDto.builder()
                                .tripDate(LocalDate.of(2023, 8, 8))
                                .distance(101L)
                                .allowances(
                                        DailyAllowance.builder()
                                                .starDate(LocalDate.of(2023, 8, 8))
                                                .endDate(LocalDate.of(2023, 8, 10))
                                                .excludeDays(List.of(LocalDate.of(2023, 8, 9)))
                                                .build())
                                .receipt(
                                        List.of(
                                                ReceiptDto.builder()
                                                        .receiptType(ReceiptType.TRAIN)
                                                        .expense(BigDecimal.valueOf(10))
                                                        .build()))
                                .build(),
                        2),
                Arguments.of(
                        UserRequestBodyDto.builder()
                                .tripDate(LocalDate.of(2023, 8, 8))
                                .distance(101L)
                                .allowances(
                                        DailyAllowance.builder()
                                                .starDate(LocalDate.of(2023, 8, 8))
                                                .endDate(LocalDate.of(2023, 8, 10))
                                                .excludeDays(List.of(LocalDate.of(2023, 8, 9)))
                                                .build())
                                .receipt(
                                        List.of(
                                                ReceiptDto.builder()
                                                        .receiptType(ReceiptType.PLANE)
                                                        .expense(BigDecimal.valueOf(10))
                                                        .build()))
                                .build(),
                        3),
                Arguments.of(UserRequestBodyDto.builder().build(), 2));
    }

    private void givenLimits() {
        when(adminService.getLimits())
                .thenReturn(
                        AdminResponseBodyDto.builder()
                                .dailyAllowance(BigDecimal.valueOf(15))
                                .distance(100L)
                                .carMileage(BigDecimal.valueOf(0.3))
                                .totalReimbursement(BigDecimal.valueOf(10000))
                                .receipts(
                                        List.of(
                                                ReceiptData.builder()
                                                        .receiptType(ReceiptType.TRAIN)
                                                        .isActive(TRUE)
                                                        .limit(BigDecimal.valueOf(100))
                                                        .build()))
                                .build());
    }
}
