package com.barjb.application.admin.view;

import com.barjb.application.admin.repository.ReceiptType;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Value
@Builder
@Jacksonized
public class ReceiptData {
    ReceiptType receiptType;
    Boolean isActive;
    BigDecimal limit;
}
