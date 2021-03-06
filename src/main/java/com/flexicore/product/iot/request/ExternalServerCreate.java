package com.flexicore.product.iot.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.product.containers.request.EquipmentCreate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class ExternalServerCreate extends EquipmentCreate {
	@FieldInfo
	private String url;

	@FieldInfo
	private Long inspectIntervalMs;
	@FieldInfo
	private Long inspectAfterActivateIntervalMs;
	@JsonIgnore
	private OffsetDateTime lastInspectAttempt;
	@JsonIgnore
	private OffsetDateTime lastSuccessfulInspect;

	public String getUrl() {
		return url;
	}

	public <T extends ExternalServerCreate> T setUrl(String url) {
		this.url = url;
		return (T) this;
	}

	public Long getInspectIntervalMs() {
		return inspectIntervalMs;
	}

	public <T extends ExternalServerCreate> T setInspectIntervalMs(
			Long inspectIntervalMs) {
		this.inspectIntervalMs = inspectIntervalMs;
		return (T) this;
	}

	@JsonIgnore
	public OffsetDateTime getLastInspectAttempt() {
		return lastInspectAttempt;
	}

	public <T extends ExternalServerCreate> T setLastInspectAttempt(
			OffsetDateTime lastInspectAttempt) {
		this.lastInspectAttempt = lastInspectAttempt;
		return (T) this;
	}

	@JsonIgnore
	public OffsetDateTime getLastSuccessfulInspect() {
		return lastSuccessfulInspect;
	}

	public <T extends ExternalServerCreate> T setLastSuccessfulInspect(
			OffsetDateTime lastSuccessfulInspect) {
		this.lastSuccessfulInspect = lastSuccessfulInspect;
		return (T) this;
	}

	public Long getInspectAfterActivateIntervalMs() {
		return inspectAfterActivateIntervalMs;
	}

	public <T extends ExternalServerCreate> T setInspectAfterActivateIntervalMs(
			Long inspectAfterActivateIntervalMs) {
		this.inspectAfterActivateIntervalMs = inspectAfterActivateIntervalMs;
		return (T) this;
	}
}
