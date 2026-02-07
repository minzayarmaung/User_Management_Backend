package com.project.user_management.data.enums;

import com.project.user_management.common.constant.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ROLE implements BaseEnum<Integer> {
    USER(1),
    ADMIN(2);

    private final int value;

    @Override
    public Integer getValue() {
        return value;
    }
}