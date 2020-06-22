package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.model.Model;

public class ManufacturerUpdate extends ManufacturerCreate {
	private String id;
	@JsonIgnore
	private Manufacturer manufacturer;

	public String getId() {
		return id;
	}

	public ManufacturerUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public ManufacturerUpdate setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
		return this;
	}
}
