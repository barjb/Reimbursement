package com.barjb.application.user.view;

import com.barjb.application.admin.view.ReceiptData;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
@Jacksonized
public class LimitsView {
    String uuid;
    BigDecimal dailyAllowance;
    BigDecimal carMileage;
    Long distance;
    BigDecimal totalReimbursement;
    List<ReceiptData> receipts;
}

