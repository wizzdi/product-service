package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EquipmentToGroupFiltering extends FilteringInformationHolder {

	private Set<String> equipmentIds = new HashSet<>();
	@JsonIgnore
	private List<Equipment> equipments;
	private Set<String> groupIds = new HashSet<>();
	@JsonIgnore
	private List<EquipmentGroup> groups;
	private boolean raw;

	public Set<String> getEquipmentIds() {
		return equipmentIds;
	}

	public <T extends EquipmentToGroupFiltering> T setEquipmentIds(
			Set<String> equipmentIds) {
		this.equipmentIds = equipmentIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Equipment> getEquipments() {
		return equipments;
	}

	public <T extends EquipmentToGroupFiltering> T setEquipments(
			List<Equipment> equipments) {
		this.equipments = equipments;
		return (T) this;
	}

	public Set<String> getGroupIds() {
		return groupIds;
	}

	public <T extends EquipmentToGroupFiltering> T setGroupIds(
			Set<String> groupIds) {
		this.groupIds = groupIds;
		return (T) this;
	}

	@JsonIgnore
	public List<EquipmentGroup> getGroups() {
		return groups;
	}

	public <T extends EquipmentToGroupFiltering> T setGroups(
			List<EquipmentGroup> groups) {
		this.groups = groups;
		return (T) this;
	}

	public boolean isRaw() {
		return raw;
	}

	public <T extends EquipmentToGroupFiltering> T setRaw(boolean raw) {
		this.raw = raw;
		return (T) this;
	}
}
