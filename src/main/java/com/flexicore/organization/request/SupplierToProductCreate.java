package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Supplier;
import com.flexicore.product.model.Product;

public class SupplierToProductCreate {
	private String supplierId;
	@JsonIgnore
	private Supplier supplier;
	private String productId;
	@JsonIgnore
	private Product product;
	private Double price;

	public String getSupplierId() {
		return supplierId;
	}

	public SupplierToProductCreate setSupplierId(String supplierId) {
		this.supplierId = supplierId;
		return this;
	}
	@JsonIgnore
	public Supplier getSupplier() {
		return supplier;
	}

	public SupplierToProductCreate setSupplier(Supplier supplier) {
		this.supplier = supplier;
		return this;
	}

	public String getProductId() {
		return productId;
	}

	public SupplierToProductCreate setProductId(String productId) {
		this.productId = productId;
		return this;
	}
	@JsonIgnore
	public Product getProduct() {
		return product;
	}

	public SupplierToProductCreate setProduct(Product product) {
		this.product = product;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public SupplierToProductCreate setPrice(Double price) {
		this.price = price;
		return this;
	}
}
