package com.flexicore.product.containers.request;

public class ProductStatusCreate {

	private String name;
	private String description;

	public String getName() {
		return name;
	}

	public ProductStatusCreate setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ProductStatusCreate setDescription(String description) {
		this.description = description;
		return this;
	}

}
