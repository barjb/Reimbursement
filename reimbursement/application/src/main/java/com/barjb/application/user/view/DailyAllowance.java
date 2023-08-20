package com.barjb.application.user.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Value
public class DailyAllowance {
    LocalDate starDate;
    LocalDate endDate;
    List<LocalDate> excludeDays;
}
