package com.barjb.application.admin.view;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@Jacksonized
public class AdminResponseBodyDto {
    String uuid;
    BigDecimal dailyAllowance;
    BigDecimal carMileage;
    Long distance;
    BigDecimal totalReimbursement;
    List<ReceiptData> receipts;
}
