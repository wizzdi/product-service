package com.flexicore.product.response;

public class AckEventsResponse {
	private long updated;

	public long getUpdated() {
		return updated;
	}

	public <T extends AckEventsResponse> T setUpdated(long updated) {
		this.updated = updated;
		return (T) this;
	}
}
