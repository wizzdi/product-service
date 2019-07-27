package com.flexicore.product.request;

import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.EquipmentByStatusEvent;

public class EquipmentStatusEventFilter extends EventFiltering {

    @Override
    public String getEventType() {
        return EquipmentByStatusEvent.class.getCanonicalName();
    }
}
