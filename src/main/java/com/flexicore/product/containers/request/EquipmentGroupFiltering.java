package com.flexicore.product.containers.request;

public class EquipmentGroupFiltering extends EquipmentFiltering {

    private int precision;

    public int getPrecision() {
        return precision;
    }

    public EquipmentGroupFiltering setPrecision(int precision) {
        this.precision = precision;
        return this;
    }
}
