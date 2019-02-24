package com.flexicore.product.containers.response;

public class EquipmentSpecificTypeGroup {

    private String specificType;
    private long count;

    public EquipmentSpecificTypeGroup(String specificType, long count) {
        this.specificType = specificType;
        this.count = count;
    }

    public String getSpecificType() {
        return specificType;
    }

    public <T extends EquipmentSpecificTypeGroup> T setSpecificType(String specificType) {
        this.specificType = specificType;
        return (T) this;
    }


    public long getCount() {
        return count;
    }

    public <T extends EquipmentSpecificTypeGroup> T setCount(long count) {
        this.count = count;
        return (T) this;
    }
}
