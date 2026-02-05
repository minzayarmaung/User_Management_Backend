package com.project.user_management.common.converter;

import com.project.user_management.common.constant.Status;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter extends BaseEnumConverter<Status, Integer>{

    public StatusConverter() {
        super(Status.class);
    }
}