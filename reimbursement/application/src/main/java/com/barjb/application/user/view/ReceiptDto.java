package com.barjb.application.user.view;

import com.barjb.application.admin.repository.ReceiptType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Value
public class ReceiptDto {
    BigDecimal expense;
    ReceiptType receiptType;
}
