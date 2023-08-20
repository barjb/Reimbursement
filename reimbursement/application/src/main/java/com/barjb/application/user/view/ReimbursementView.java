package com.barjb.application.user.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Value
public class ReimbursementView {
    String UUID;
}
