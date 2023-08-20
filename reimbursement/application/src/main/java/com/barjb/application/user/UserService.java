package com.barjb.application.user;

import com.barjb.application.admin.repository.LimitsDao;
import com.barjb.application.user.repository.ReimbursementDao;
import com.barjb.application.user.view.LimitsView;
import com.barjb.application.user.view.ReimbursementView;
import com.barjb.application.user.view.UserRequestBodyDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {
    private final ReimbursementDao reimbursementDao;

    private final LimitsDao limitsDao;

    public ReimbursementView saveReimbursement(UserRequestBodyDto userRequestDto) {
        var savedData = reimbursementDao.save(userRequestDto);
        return new ReimbursementView(savedData.getUuid());
    }

    public LimitsView getLimitsViewUser() {
        var limitsAdmin = limitsDao.getLimits();
        return LimitsView.builder()
                .uuid(limitsAdmin.getUuid())
                .dailyAllowance(limitsAdmin.getDailyAllowance())
                .carMileage(limitsAdmin.getCarMileage())
                .distance(limitsAdmin.getDistance())
                .totalReimbursement(limitsAdmin.getTotalReimbursement())
                .receipts(limitsAdmin.getReceipts())
                .build();
    }
}
