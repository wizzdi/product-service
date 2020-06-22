package com.flexicore.product.request;

import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.model.dynamic.ExecutionParametersHolder;
import com.flexicore.product.containers.request.EquipmentUpdate;

public class UpdateEquipmentParameters extends ExecutionParametersHolder {

	@FieldInfo(mandatory = true, displayName = "Update Container")
	private EquipmentUpdate equipmentUpdate;

	public EquipmentUpdate getEquipmentUpdate() {
		return equipmentUpdate;
	}

	public <T extends UpdateEquipmentParameters> T setEquipmentUpdate(
			EquipmentUpdate equipmentUpdate) {
		this.equipmentUpdate = equipmentUpdate;
		return (T) this;
	}
}
