package com.barjb.application.admin;

import com.barjb.application.admin.repository.LimitsDao;
import com.barjb.application.admin.view.AdminRequestBodyDto;
import com.barjb.application.admin.view.AdminResponseBodyDto;
import com.barjb.application.admin.view.LimitsView;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminService {

    private final LimitsDao limitsDao;

    public LimitsView saveAdminLimits(AdminRequestBodyDto adminRequestBodyDto) {
        var limitsData = limitsDao.saveLimits(adminRequestBodyDto);

        return LimitsView.builder().uuid(limitsData.getUuid()).build();
    }

    public AdminResponseBodyDto getLimits() {
        return limitsDao.getLimits();
    }
}
