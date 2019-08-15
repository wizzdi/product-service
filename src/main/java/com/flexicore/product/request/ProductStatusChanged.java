package com.flexicore.product.request;

import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.Event;

public class ProductStatusChanged extends Event {

    public ProductStatusChanged() {
    }

    public ProductStatusChanged(Equipment equipment) {
        super(equipment);
    }


}
