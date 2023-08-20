package com.barjb.application.admin.repository;

import com.barjb.application.admin.view.ReceiptData;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Value
@Jacksonized
public class LimitsData {
    String uuid;
    BigDecimal dailyAllowance;
    BigDecimal carMileage;
    Long distance;
    BigDecimal totalReimbursement;
    List<ReceiptData> receipts;
}
