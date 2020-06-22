package com.flexicore.product.model;

import com.flexicore.model.Baseclass;
import com.flexicore.product.containers.response.EquipmentStatusGroup;
import org.bson.codecs.pojo.annotations.BsonId;

public class DetailedEquipmentStatus {

	@BsonId
	private String id;
	private String equipmentId;
	private String equipmentByStatusEntry;

	public DetailedEquipmentStatus() {
		this.id = Baseclass.getBase64ID();
	}

	public String getId() {
		return id;
	}

	public DetailedEquipmentStatus setId(String id) {
		this.id = id;
		return this;
	}

	public String getEquipmentId() {
		return equipmentId;
	}

	public <T extends DetailedEquipmentStatus> T setEquipmentId(
			String equipmentId) {
		this.equipmentId = equipmentId;
		return (T) this;
	}

	public String getEquipmentByStatusEntry() {
		return equipmentByStatusEntry;
	}

	public <T extends DetailedEquipmentStatus> T setEquipmentByStatusEntry(
			String equipmentByStatusEntry) {
		this.equipmentByStatusEntry = equipmentByStatusEntry;
		return (T) this;
	}
}
