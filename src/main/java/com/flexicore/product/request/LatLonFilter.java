package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.model.MultiLatLonEquipment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LatLonFilter extends FilteringInformationHolder {

	private Set<String> multiLatLonEquipmentIds = new HashSet<>();
	@JsonIgnore
	private List<? extends MultiLatLonEquipment> multiLatLonEquipments;

	public Set<String> getMultiLatLonEquipmentIds() {
		return multiLatLonEquipmentIds;
	}

	public <T extends LatLonFilter> T setMultiLatLonEquipmentIds(
			Set<String> multiLatLonEquipmentIds) {
		this.multiLatLonEquipmentIds = multiLatLonEquipmentIds;
		return (T) this;
	}

	@JsonIgnore
	public List<? extends MultiLatLonEquipment> getMultiLatLonEquipments() {
		return multiLatLonEquipments;
	}

	public <T extends LatLonFilter> T setMultiLatLonEquipments(
			List<? extends MultiLatLonEquipment> multiLatLonEquipments) {
		this.multiLatLonEquipments = multiLatLonEquipments;
		return (T) this;
	}
}
