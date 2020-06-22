package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Equipment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnableEquipments {
	private Set<String> equipmentIds = new HashSet<>();
	private boolean enable;
	@JsonIgnore
	private List<Equipment> equipmentList;

	public Set<String> getEquipmentIds() {
		return equipmentIds;
	}

	public EnableEquipments setEquipmentIds(Set<String> equipmentIds) {
		this.equipmentIds = equipmentIds;
		return this;
	}
	@JsonIgnore
	public List<Equipment> getEquipmentList() {
		return equipmentList;
	}

	public EnableEquipments setEquipmentList(List<Equipment> equipmentList) {
		this.equipmentList = equipmentList;
		return this;
	}

	public boolean isEnable() {
		return enable;
	}

	public EnableEquipments setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}
}
