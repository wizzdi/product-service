package com.flexicore.product.request;

import com.flexicore.product.model.ProductType;

public class EquipmentAndType {

	private String id;
	private ProductType productType;

	public EquipmentAndType(String id, ProductType productType) {
		this.id = id;
		this.productType = productType;
	}

	public String getId() {
		return id;
	}

	public <T extends EquipmentAndType> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	public ProductType getProductType() {
		return productType;
	}

	public <T extends EquipmentAndType> T setProductType(ProductType productType) {
		this.productType = productType;
		return (T) this;
	}
}
