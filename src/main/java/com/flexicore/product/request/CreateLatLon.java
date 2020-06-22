package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.MultiLatLonEquipment;

public class CreateLatLon {

	private Integer ordinal;
	private String contextString;
	private Double lat;
	private Double lon;
	private String multiLatLonEquipmentId;
	@JsonIgnore
	private MultiLatLonEquipment multiLatLonEquipment;
	private Boolean softDelete;

	public Double getLat() {
		return lat;
	}

	public <T extends CreateLatLon> T setLat(Double lat) {
		this.lat = lat;
		return (T) this;
	}

	public Double getLon() {
		return lon;
	}

	public <T extends CreateLatLon> T setLon(Double lon) {
		this.lon = lon;
		return (T) this;
	}

	public String getMultiLatLonEquipmentId() {
		return multiLatLonEquipmentId;
	}

	public <T extends CreateLatLon> T setMultiLatLonEquipmentId(
			String multiLatLonEquipmentId) {
		this.multiLatLonEquipmentId = multiLatLonEquipmentId;
		return (T) this;
	}

	@JsonIgnore
	public MultiLatLonEquipment getMultiLatLonEquipment() {
		return multiLatLonEquipment;
	}

	public <T extends CreateLatLon> T setMultiLatLonEquipment(
			MultiLatLonEquipment multiLatLonEquipment) {
		this.multiLatLonEquipment = multiLatLonEquipment;
		return (T) this;
	}

	public Integer getOrdinal() {
		return ordinal;
	}

	public <T extends CreateLatLon> T setOrdinal(Integer ordinal) {
		this.ordinal = ordinal;
		return (T) this;
	}

	public String getContextString() {
		return contextString;
	}

	public <T extends CreateLatLon> T setContextString(String contextString) {
		this.contextString = contextString;
		return (T) this;
	}

	public Boolean getSoftDelete() {
		return softDelete;
	}

	public <T extends CreateLatLon> T setSoftDelete(Boolean softDelete) {
		this.softDelete = softDelete;
		return (T) this;
	}
}
