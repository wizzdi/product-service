package com.flexicore.product.request;

public class ProductToStatusMassUpdate {
	private ProductToStatusFilter productToStatusFilter;
	private boolean enable;

	public ProductToStatusFilter getProductToStatusFilter() {
		return productToStatusFilter;
	}

	public <T extends ProductToStatusMassUpdate> T setProductToStatusFilter(
			ProductToStatusFilter productToStatusFilter) {
		this.productToStatusFilter = productToStatusFilter;
		return (T) this;
	}

	public boolean isEnable() {
		return enable;
	}

	public <T extends ProductToStatusMassUpdate> T setEnable(boolean enable) {
		this.enable = enable;
		return (T) this;
	}
}
