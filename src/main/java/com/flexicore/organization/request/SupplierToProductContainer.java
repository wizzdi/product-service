package com.flexicore.organization.request;

import com.flexicore.organization.model.SupplierToProduct;

public class SupplierToProductContainer {
	private String id;
	private String supplierId;
	private String productId;
	private double price;

	public SupplierToProductContainer(SupplierToProduct supplierToProduct) {
		this.id = supplierToProduct.getId();
		this.supplierId = supplierToProduct.getSupplier() != null
				? supplierToProduct.getSupplier().getId()
				: null;
		this.productId = supplierToProduct.getProduct() != null
				? supplierToProduct.getProduct().getId()
				: null;
		this.price = supplierToProduct.getPrice();
	}

	public SupplierToProductContainer() {
	}

	public String getSupplierId() {
		return supplierId;
	}

	public SupplierToProductContainer setSupplierId(String supplierId) {
		this.supplierId = supplierId;
		return this;
	}

	public String getProductId() {
		return productId;
	}

	public SupplierToProductContainer setProductId(String productId) {
		this.productId = productId;
		return this;
	}

	public double getPrice() {
		return price;
	}

	public SupplierToProductContainer setPrice(double price) {
		this.price = price;
		return this;
	}

	public String getId() {
		return id;
	}

	public <T extends SupplierToProductContainer> T setId(String id) {
		this.id = id;
		return (T) this;
	}
}
