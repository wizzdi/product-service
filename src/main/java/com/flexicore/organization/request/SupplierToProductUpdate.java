package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.SupplierToProduct;

public class SupplierToProductUpdate extends SupplierToProductCreate {
	private String id;
	@JsonIgnore
	private SupplierToProduct supplierToProduct;

	public String getId() {
		return id;
	}

	public SupplierToProductUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public SupplierToProduct getSupplierToProduct() {
		return supplierToProduct;
	}

	public SupplierToProductUpdate setSupplierToProduct(
			SupplierToProduct supplierToProduct) {
		this.supplierToProduct = supplierToProduct;
		return this;
	}
}
