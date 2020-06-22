package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.Product;
import com.flexicore.product.model.ProductStatus;

public class ProductStatusToProductCreate {

	private String productId;
	private String productStatusId;

	@JsonIgnore
	private Product product;
	@JsonIgnore
	private ProductStatus productStatus;

	public String getProductId() {
		return productId;
	}

	public ProductStatusToProductCreate setProductId(String productId) {
		this.productId = productId;
		return this;
	}

	public String getProductStatusId() {
		return productStatusId;
	}

	public ProductStatusToProductCreate setProductStatusId(
			String productStatusId) {
		this.productStatusId = productStatusId;
		return this;
	}
	@JsonIgnore
	public Product getProduct() {
		return product;
	}

	public ProductStatusToProductCreate setProduct(Product product) {
		this.product = product;
		return this;
	}
	@JsonIgnore
	public ProductStatus getProductStatus() {
		return productStatus;
	}

	public ProductStatusToProductCreate setProductStatus(
			ProductStatus productStatus) {
		this.productStatus = productStatus;
		return this;
	}
}
