package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.product.model.ProductTypeToProductStatus;

public class StatusLinksToImageCreate {

	private String name;
	private String statusLinkId;
	@JsonIgnore
	private ProductTypeToProductStatus productTypeToProductStatus;
	private String imageId;
	@JsonIgnore
	private FileResource image;

	public String getName() {
		return name;
	}

	public <T extends StatusLinksToImageCreate> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	public String getStatusLinkId() {
		return statusLinkId;
	}

	public <T extends StatusLinksToImageCreate> T setStatusLinkId(
			String statusLinkId) {
		this.statusLinkId = statusLinkId;
		return (T) this;
	}

	@JsonIgnore
	public ProductTypeToProductStatus getProductTypeToProductStatus() {
		return productTypeToProductStatus;
	}

	public <T extends StatusLinksToImageCreate> T setProductTypeToProductStatus(
			ProductTypeToProductStatus productTypeToProductStatus) {
		this.productTypeToProductStatus = productTypeToProductStatus;
		return (T) this;
	}

	public String getImageId() {
		return imageId;
	}

	public <T extends StatusLinksToImageCreate> T setImageId(String imageId) {
		this.imageId = imageId;
		return (T) this;
	}

	@JsonIgnore
	public FileResource getImage() {
		return image;
	}

	public <T extends StatusLinksToImageCreate> T setImage(FileResource image) {
		this.image = image;
		return (T) this;
	}
}
