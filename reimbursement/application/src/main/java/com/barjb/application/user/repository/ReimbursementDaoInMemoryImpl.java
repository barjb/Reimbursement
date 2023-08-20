package com.barjb.application.user.repository;

import com.barjb.application.user.view.UserRequestBodyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.barjb.application.common.Constants.*;
import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class ReimbursementDaoInMemoryImpl implements ReimbursementDao {

    private final Map<String, ReimbursementData> rows = new HashMap<>();

    private final ObjectMapper objectMapper;

    @Override
    public ReimbursementData save(UserRequestBodyDto userRequestDto) {
        log.info("Save user form to data structure = {}", userRequestDto);
        var uuid = UUID.randomUUID().toString().replace(DASH, EMPTY);

        // TODO info: https://www.baeldung.com/java-deep-copy

        UserRequestBodyDto deepCopy;
        try {
            objectMapper.registerModule(new JSR310Module());
            deepCopy =
                    objectMapper.readValue(
                            objectMapper.writeValueAsString(userRequestDto), UserRequestBodyDto.class);

            log.info("Saved user form to data structure = {}", deepCopy);
        } catch (JsonProcessingException e) {
            log.warn("JProcessingException error occurred", e);
            throw new IllegalArgumentException(
                    format(INVALID_PROCESSING_DURING_DEEP_COPY, userRequestDto), e);
        }
        rows.put(
                uuid,
                ReimbursementData.builder()
                        .uuid(uuid)
                        .distance(deepCopy.getDistance())
                        .tripDate(deepCopy.getTripDate())
                        .receipt(deepCopy.getReceipt())
                        .allowances(deepCopy.getAllowances())
                        .build());
        return rows.get(uuid);
    }
}
