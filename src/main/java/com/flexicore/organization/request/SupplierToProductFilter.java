package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.organization.model.Supplier;
import com.flexicore.product.model.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SupplierToProductFilter extends FilteringInformationHolder {

	private Set<String> productIds = new HashSet<>();
	@JsonIgnore
	private List<Product> products;
	private Set<String> supplierIds = new HashSet<>();
	@JsonIgnore
	private List<Supplier> suppliers;

	public Set<String> getProductIds() {
		return productIds;
	}

	public SupplierToProductFilter setProductIds(Set<String> productIds) {
		this.productIds = productIds;
		return this;
	}
	@JsonIgnore
	public List<Product> getProducts() {
		return products;
	}

	public SupplierToProductFilter setProducts(List<Product> products) {
		this.products = products;
		return this;
	}

	public Set<String> getSupplierIds() {
		return supplierIds;
	}

	public SupplierToProductFilter setSupplierIds(Set<String> supplierIds) {
		this.supplierIds = supplierIds;
		return this;
	}

	@JsonIgnore
	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public SupplierToProductFilter setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
		return this;
	}
}
