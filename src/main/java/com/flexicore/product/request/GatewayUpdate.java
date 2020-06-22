package com.flexicore.product.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.product.containers.request.GatewayCreate;
import com.flexicore.product.model.Gateway;

public class GatewayUpdate extends GatewayCreate {

	private String id;
	@JsonIgnore
	private Gateway gatewayToUpdate;

	public String getId() {
		return id;
	}

	public <T extends GatewayUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Gateway getGatewayToUpdate() {
		return gatewayToUpdate;
	}

	public <T extends GatewayUpdate> T setGatewayToUpdate(
			Gateway gatewayToUpdate) {
		this.gatewayToUpdate = gatewayToUpdate;
		return (T) this;
	}
}
