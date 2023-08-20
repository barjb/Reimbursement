package com.barjb.application.user.repository;

import com.barjb.application.user.view.UserRequestBodyDto;

public interface ReimbursementDao {
    ReimbursementData save(UserRequestBodyDto userRequestDto);
}
