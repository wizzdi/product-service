package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;

public class LinkToGroup {

	private String equipmentId;
	private String groupId;
	private Equipment equipment;
	private EquipmentGroup equipmentGroup;

	public String getEquipmentId() {
		return equipmentId;
	}

	public LinkToGroup setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public LinkToGroup setGroupId(String groupId) {
		this.groupId = groupId;
		return this;
	}

	@JsonIgnore
	public Equipment getEquipment() {
		return equipment;
	}

	public LinkToGroup setEquipment(Equipment equipment) {
		this.equipment = equipment;
		return this;
	}

	@JsonIgnore
	public EquipmentGroup getEquipmentGroup() {
		return equipmentGroup;
	}

	public LinkToGroup setEquipmentGroup(EquipmentGroup equipmentGroup) {
		this.equipmentGroup = equipmentGroup;
		return this;
	}
}
