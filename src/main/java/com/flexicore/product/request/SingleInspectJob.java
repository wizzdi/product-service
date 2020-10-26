package com.flexicore.product.request;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class SingleInspectJob<S extends com.flexicore.iot.ExternalServer, C extends com.flexicore.product.request.ConnectionHolder<S>> {

	private String id;
	private String externalServerId;
	private String configurationId;
	private OffsetDateTime timeToInspect;

	public SingleInspectJob(String id, String externalServerId,
			String configurationId, OffsetDateTime timeToInspect) {
		this.id = id;
		this.externalServerId = externalServerId;
		this.configurationId = configurationId;
		this.timeToInspect = timeToInspect;
	}

	public SingleInspectJob() {
	}

	private C connectionHolder;

	public C getConnectionHolder() {
		return connectionHolder;
	}

	public <T extends SingleInspectJob<S, C>> T setConnectionHolder(
			C connectionHolder) {
		this.connectionHolder = connectionHolder;
		return (T) this;
	}

	public String getId() {
		return id;
	}

	public OffsetDateTime getTimeToInspect() {
		return timeToInspect;
	}

	public String getExternalServerId() {
		return externalServerId;
	}

	public String getConfigurationId() {
		return configurationId;
	}
}
