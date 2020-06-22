package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.product.model.ProductStatus;
import com.flexicore.product.model.ProductType;
import com.flexicore.product.model.ProductTypeToProductStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatusLinksToImageFilter extends FilteringInformationHolder {

	private Set<String> statusIds = new HashSet<>();
	@JsonIgnore
	private List<ProductStatus> status;
	private Set<String> productTypeIds = new HashSet<>();
	@JsonIgnore
	private List<ProductType> productTypes;
	private Set<String> statusLinkIds = new HashSet<>();
	@JsonIgnore
	private List<ProductTypeToProductStatus> statusLinks;

	public Set<String> getStatusIds() {
		return statusIds;
	}

	public <T extends StatusLinksToImageFilter> T setStatusIds(
			Set<String> statusIds) {
		this.statusIds = statusIds;
		return (T) this;
	}

	public Set<String> getProductTypeIds() {
		return productTypeIds;
	}

	public <T extends StatusLinksToImageFilter> T setProductTypeIds(
			Set<String> productTypeIds) {
		this.productTypeIds = productTypeIds;
		return (T) this;
	}

	public Set<String> getStatusLinkIds() {
		return statusLinkIds;
	}

	public <T extends StatusLinksToImageFilter> T setStatusLinkIds(
			Set<String> statusLinkIds) {
		this.statusLinkIds = statusLinkIds;
		return (T) this;
	}

	@JsonIgnore
	public List<ProductStatus> getStatus() {
		return status;
	}

	public <T extends StatusLinksToImageFilter> T setStatus(
			List<ProductStatus> status) {
		this.status = status;
		return (T) this;
	}
	@JsonIgnore
	public List<ProductType> getProductTypes() {
		return productTypes;
	}

	public <T extends StatusLinksToImageFilter> T setProductTypes(
			List<ProductType> productTypes) {
		this.productTypes = productTypes;
		return (T) this;
	}

	@JsonIgnore
	public List<ProductTypeToProductStatus> getStatusLinks() {
		return statusLinks;
	}

	public <T extends StatusLinksToImageFilter> T setStatusLinks(
			List<ProductTypeToProductStatus> statusLinks) {
		this.statusLinks = statusLinks;
		return (T) this;
	}
}
