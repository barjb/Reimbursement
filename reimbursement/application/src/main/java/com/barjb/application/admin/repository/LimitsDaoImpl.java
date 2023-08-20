package com.barjb.application.admin.repository;

import com.barjb.application.admin.view.AdminRequestBodyDto;
import com.barjb.application.admin.view.AdminResponseBodyDto;
import com.barjb.application.admin.view.ReceiptData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.barjb.application.common.Constants.*;
import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class LimitsDaoImpl implements LimitsDao {

    public static final String CONSTANT_UUID = "1";
    private final ObjectMapper objectMapper;

    private LimitsData row =
            LimitsData.builder()
                    .uuid(CONSTANT_UUID)
                    .dailyAllowance(BigDecimal.valueOf(15))
                    .carMileage(BigDecimal.valueOf(0.3))
                    .distance(100L)
                    .totalReimbursement(BigDecimal.valueOf(10000))
                    .receipts(
                            List.of(
                                    ReceiptData.builder()
                                            .limit(BigDecimal.valueOf(500))
                                            .receiptType(ReceiptType.PLANE)
                                            .isActive(true)
                                            .build(),
                                    ReceiptData.builder()
                                            .limit(BigDecimal.valueOf(200))
                                            .receiptType(ReceiptType.HOTEL)
                                            .isActive(true)
                                            .build(),
                                    ReceiptData.builder()
                                            .limit(BigDecimal.valueOf(100))
                                            .receiptType(ReceiptType.TAXI)
                                            .isActive(true)
                                            .build(),
                                    ReceiptData.builder()
                                            .limit(BigDecimal.valueOf(100))
                                            .receiptType(ReceiptType.TRAIN)
                                            .isActive(true)
                                            .build()))
                    .build();

    @Override
    public LimitsData saveLimits(AdminRequestBodyDto adminRequestBodyDto) {
        log.info("Save limits form to data structure = {}", adminRequestBodyDto);
        var uuid = UUID.randomUUID().toString().replace(DASH, EMPTY);

        // TODO info: https://www.baeldung.com/java-deep-copy

        AdminRequestBodyDto deepCopy;
        try {
            objectMapper.registerModule(new JSR310Module());
            deepCopy =
                    objectMapper.readValue(
                            objectMapper.writeValueAsString(adminRequestBodyDto), AdminRequestBodyDto.class);

            log.info("Saved limits form to data structure = {}", deepCopy);
        } catch (JsonProcessingException e) {
            log.info("JProcessingException = {}", e.getMessage());
            throw new IllegalArgumentException(
                    format(INVALID_PROCESSING_DURING_DEEP_COPY, adminRequestBodyDto), e);
        }

        this.row =
                LimitsData.builder()
                        .dailyAllowance(deepCopy.getDailyAllowance())
                        .distance(deepCopy.getDistance())
                        .receipts(deepCopy.getReceipts())
                        .carMileage(deepCopy.getCarMileage())
                        .totalReimbursement(deepCopy.getTotalReimbursement())
                        .uuid(uuid)
                        .build();

        return row;
    }

    @Override
    public AdminResponseBodyDto getLimits() {

        LimitsData deepCopy;
        try {
            objectMapper.registerModule(new JSR310Module());
            deepCopy = objectMapper.readValue(objectMapper.writeValueAsString(row), LimitsData.class);

        } catch (JsonProcessingException e) {
            log.warn("JProcessingException error occurred", e);
            throw new IllegalArgumentException(format(INVALID_PROCESSING_DURING_DEEP_COPY, row), e);
        }
        log.info("Get limit row = {}", deepCopy);

        return AdminResponseBodyDto.builder()
                .dailyAllowance(deepCopy.getDailyAllowance())
                .distance(deepCopy.getDistance())
                .receipts(deepCopy.getReceipts())
                .carMileage(deepCopy.getCarMileage())
                .totalReimbursement(deepCopy.getTotalReimbursement())
                .uuid(deepCopy.getUuid())
                .build();
    }
}
