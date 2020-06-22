package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.MultiLatLonEquipment;

import java.util.List;

public class MassUpsertLatLonRequest {

	private String multiLatLonEquipmentId;
	@JsonIgnore
	private MultiLatLonEquipment multiLatLonEquipment;

	private List<LatLonContainer> list;
	private String contextString;

	public List<LatLonContainer> getList() {
		return list;
	}

	public <T extends MassUpsertLatLonRequest> T setList(
			List<LatLonContainer> list) {
		this.list = list;
		return (T) this;
	}

	public String getMultiLatLonEquipmentId() {
		return multiLatLonEquipmentId;
	}

	public <T extends MassUpsertLatLonRequest> T setMultiLatLonEquipmentId(
			String multiLatLonEquipmentId) {
		this.multiLatLonEquipmentId = multiLatLonEquipmentId;
		return (T) this;
	}

	@JsonIgnore
	public MultiLatLonEquipment getMultiLatLonEquipment() {
		return multiLatLonEquipment;
	}

	public <T extends MassUpsertLatLonRequest> T setMultiLatLonEquipment(
			MultiLatLonEquipment multiLatLonEquipment) {
		this.multiLatLonEquipment = multiLatLonEquipment;
		return (T) this;
	}

	public String getContextString() {
		return contextString;
	}

	public <T extends MassUpsertLatLonRequest> T setContextString(
			String contextString) {
		this.contextString = contextString;
		return (T) this;
	}
}
