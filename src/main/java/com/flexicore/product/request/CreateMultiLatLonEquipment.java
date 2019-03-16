package com.flexicore.product.request;

import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.product.containers.request.EquipmentCreate;

public class CreateMultiLatLonEquipment extends EquipmentCreate {

    @FieldInfo(displayName = "contextString",description = "Context String")

    private String contextString;

    public String getContextString() {
        return contextString;
    }

    public <T extends CreateMultiLatLonEquipment> T setContextString(String contextString) {
        this.contextString = contextString;
        return (T) this;
    }
}
