package com.flexicore.product.response;

import com.flexicore.product.model.Equipment;

import java.util.ArrayList;
import java.util.List;

public class GenericInspectResponse {
    private boolean success;
    private List<Equipment> connectedEquipment=new ArrayList<>();


    public boolean isSuccess() {
        return success;
    }

    public <T extends GenericInspectResponse> T setSuccess(boolean success) {
        this.success = success;
        return (T) this;
    }

    public List<Equipment> getConnectedEquipment() {
        return connectedEquipment;
    }

    public <T extends GenericInspectResponse> T setConnectedEquipment(List<Equipment> connectedEquipment) {
        this.connectedEquipment = connectedEquipment;
        return (T) this;
    }

    @Override
    public String toString() {
        return "GenericInspectResponse{" +
                "success=" + success +
                ", numberConnectedEquipment=" + (connectedEquipment!=null?connectedEquipment.size():0) +
                '}';
    }
}
