package com.flexicore.product.containers.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flexicore.model.FileResource;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.ProductStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EquipmentShort {

	private String id;
	private String name;
	private double lon;
	private double lat;
	private String type;
	private List<ProductStatus> currentStatus;
	private Map<String, String> iconMap;
	private String defaultIcon;

	public EquipmentShort() {
	}

	public EquipmentShort(Equipment other, List<ProductStatus> statuses,
			Map<String, String> iconMap) {
		this.id = other.getId();
		this.name = other.getName();
		this.lon = other.getLon();
		this.lat = other.getLat();
		this.type = other.getClass().getCanonicalName();
		currentStatus = statuses != null ? statuses : new ArrayList<>();
		this.iconMap = iconMap != null ? iconMap : new HashMap<>();
		this.defaultIcon = other.getProductType() != null
				&& other.getProductType().getImage() != null ? other
				.getProductType().getImage().getId() : null;
	}

	public String getId() {
		return id;
	}

	public EquipmentShort setId(String id) {
		this.id = id;
		return this;
	}

	public double getLon() {
		return lon;
	}

	public EquipmentShort setLon(double lon) {
		this.lon = lon;
		return this;
	}

	public double getLat() {
		return lat;
	}

	public EquipmentShort setLat(double lat) {
		this.lat = lat;
		return this;
	}

	public String getType() {
		return type;
	}

	public EquipmentShort setType(String type) {
		this.type = type;
		return this;
	}

	public List<ProductStatus> getCurrentStatus() {
		return currentStatus;
	}

	public EquipmentShort setCurrentStatus(List<ProductStatus> currentStatus) {
		this.currentStatus = currentStatus;
		return this;
	}

	public String getName() {
		return name;
	}

	public EquipmentShort setName(String name) {
		this.name = name;
		return this;
	}

	public Map<String, String> getIconMap() {
		return iconMap;
	}

	public EquipmentShort setIconMap(Map<String, String> iconMap) {
		this.iconMap = iconMap;
		return this;
	}

	public String getDefaultIcon() {
		return defaultIcon;
	}

	public EquipmentShort setDefaultIcon(String defaultIcon) {
		this.defaultIcon = defaultIcon;
		return this;
	}

	@JsonProperty("json-type")
	public String getJsonType() {
		return type;

	}
}
