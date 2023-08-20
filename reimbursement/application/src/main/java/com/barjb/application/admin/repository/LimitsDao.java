package com.barjb.application.admin.repository;

import com.barjb.application.admin.view.AdminRequestBodyDto;
import com.barjb.application.admin.view.AdminResponseBodyDto;

public interface LimitsDao {
    LimitsData saveLimits(AdminRequestBodyDto adminRequestBodyDto);

    AdminResponseBodyDto getLimits();
}
