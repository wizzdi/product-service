package com.flexicore.product.request;

public class LatLonContainer {
	private double lat;
	private double lon;

	public double getLat() {
		return lat;
	}

	public <T extends LatLonContainer> T setLat(double lat) {
		this.lat = lat;
		return (T) this;
	}

	public double getLon() {
		return lon;
	}

	public <T extends LatLonContainer> T setLon(double lon) {
		this.lon = lon;
		return (T) this;
	}
}
