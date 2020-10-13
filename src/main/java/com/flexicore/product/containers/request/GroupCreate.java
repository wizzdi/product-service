package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.request.BaseclassCreate;

public class GroupCreate extends BaseclassCreate {

	private String parentId;
	private String externalId;
	@JsonIgnore
	private EquipmentGroup parent;


	@JsonIgnore
	public EquipmentGroup getParent() {
		return parent;
	}


	public String getParentId() {
		return parentId;
	}

	public <T extends GroupCreate> T setParentId(String parentId) {
		this.parentId = parentId;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends GroupCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public <T extends GroupCreate> T setParent(EquipmentGroup parent) {
		this.parent = parent;
		return (T) this;
	}
}
