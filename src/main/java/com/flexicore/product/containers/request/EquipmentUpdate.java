package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;

import java.time.LocalDateTime;

public class EquipmentUpdate extends EquipmentCreate {

	@IdRefFieldInfo(displayName = "Equipment to update", mandatory = true, refType = Equipment.class, list = false)
	private String id;
	@JsonIgnore
	private Equipment equipment;

	public String getId() {
		return id;
	}

	public EquipmentUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Equipment getEquipment() {
		return equipment;
	}

	public EquipmentUpdate setEquipment(Equipment equipment) {
		this.equipment = equipment;
		return this;
	}
}
