package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.model.Model;

public class ModelCreate {
	private String name;
	private String description;
	@JsonIgnore
	private Manufacturer manufacturer;
	private String manufacturerId;

	public ModelCreate(Model other) {
		this.name = other.getName();
		this.description = other.getDescription();
		this.manufacturer = other.getManufacturer();
		this.manufacturerId = this.manufacturer != null ? this.manufacturer
				.getId() : null;
	}

	public ModelCreate() {
	}

	public String getName() {
		return name;
	}

	public ModelCreate setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ModelCreate setDescription(String description) {
		this.description = description;
		return this;
	}

	@JsonIgnore
	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public ModelCreate setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
		return this;
	}

	public String getManufacturerId() {
		return manufacturerId;
	}

	public ModelCreate setManufacturerId(String manufacturerId) {
		this.manufacturerId = manufacturerId;
		return this;
	}
}
