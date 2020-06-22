package com.flexicore.product.request;

import com.flexicore.product.model.ProductStatus;

public class ProductStatusNoProductContainer {

	private String id;
	private String productId;
	private ProductStatus status;

	public ProductStatusNoProductContainer(String id, String productId,
			ProductStatus status) {
		this.id = id;
		this.productId = productId;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public <T extends ProductStatusNoProductContainer> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	public String getProductId() {
		return productId;
	}

	public <T extends ProductStatusNoProductContainer> T setProductId(
			String productId) {
		this.productId = productId;
		return (T) this;
	}

	public ProductStatus getStatus() {
		return status;
	}

	public <T extends ProductStatusNoProductContainer> T setStatus(
			ProductStatus status) {
		this.status = status;
		return (T) this;
	}
}
