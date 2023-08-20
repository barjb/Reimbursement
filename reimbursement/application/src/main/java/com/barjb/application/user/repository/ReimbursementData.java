package com.barjb.application.user.repository;

import com.barjb.application.user.view.DailyAllowance;
import com.barjb.application.user.view.ReceiptDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Builder
@Jacksonized
@Value
public class ReimbursementData {

    String uuid;
    LocalDate tripDate;
    List<ReceiptDto> receipt;
    DailyAllowance allowances;
    Long distance;
}
