package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.model.Tenant;
import com.flexicore.product.model.Gateway;
import com.flexicore.product.model.Model;
import com.flexicore.product.model.Product;
import com.flexicore.product.model.ProductType;
import com.flexicore.request.BaseclassCreate;

import java.time.LocalDateTime;

public class ProductCreate extends BaseclassCreate {

	@FieldInfo(description = "sku")
	private String sku;

	@IdRefFieldInfo(description = "product type", displayName = "Product Type", refType = ProductType.class, list = false)
	private String productTypeId;
	@JsonIgnore
	private ProductType ProductType;

	private String clazzName;
	@JsonIgnore
	private Class<?> clazz;

	@JsonIgnore
	private Model model;
	private String modelId;

	@JsonIgnore
	private Tenant tenant;

	public ProductCreate() {
	}

	public ProductCreate(Product other) {
		super(other);
		this.sku = other.getSku();
		this.ProductType = other.getProductType();
		this.productTypeId = this.ProductType != null ? this.ProductType
				.getId() : null;
		this.model = other.getModel();
		this.modelId = this.model != null ? this.model.getId() : null;
	}

	public String getSku() {
		return sku;
	}

	public <T extends ProductCreate> T setSku(String sku) {
		this.sku = sku;
		return (T) this;
	}

	public String getProductTypeId() {
		return productTypeId;
	}

	public <T extends ProductCreate> T setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
		return (T) this;
	}

	@JsonIgnore
	public ProductType getProductType() {
		return ProductType;
	}

	public <T extends ProductCreate> T setProductType(ProductType productType) {
		ProductType = productType;
		return (T) this;
	}

	public String getClazzName() {
		return clazzName;
	}

	public <T extends ProductCreate> T setClazzName(String clazzName) {
		this.clazzName = clazzName;
		return (T) this;
	}

	@JsonIgnore
	public Class<?> getClazz() {
		return clazz;
	}

	public <T extends ProductCreate> T setClazz(Class<?> clazz) {
		this.clazz = clazz;
		return (T) this;
	}

	@JsonIgnore
	public Model getModel() {
		return model;
	}

	public <T extends ProductCreate> T setModel(Model model) {
		this.model = model;
		return (T) this;
	}

	public String getModelId() {
		return modelId;
	}

	public <T extends ProductCreate> T setModelId(String modelId) {
		this.modelId = modelId;
		return (T) this;
	}

}
