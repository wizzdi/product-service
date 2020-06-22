package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.LatLon;

public class UpdateLatLon extends CreateLatLon {

	private String id;
	private boolean manualUpdateOrdinal;
	@JsonIgnore
	private LatLon latLon;

	public String getId() {
		return id;
	}

	public <T extends UpdateLatLon> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public LatLon getLatLon() {
		return latLon;
	}

	public <T extends UpdateLatLon> T setLatLon(LatLon latLon) {
		this.latLon = latLon;
		return (T) this;
	}

	public boolean isManualUpdateOrdinal() {
		return manualUpdateOrdinal;
	}

	public <T extends UpdateLatLon> T setManualUpdateOrdinal(
			boolean manualUpdateOrdinal) {
		this.manualUpdateOrdinal = manualUpdateOrdinal;
		return (T) this;
	}
}
