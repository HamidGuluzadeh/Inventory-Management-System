package com.auspify_tech.inventory_management_system.dto;

import com.auspify_tech.inventory_management_system.model.SuccessStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuccessDto <T> {
    String status;
    T data;

    public SuccessDto(SuccessStatus successStatus, T data) {
        this.status = successStatus.name();
        this.data = data;
    }
}
