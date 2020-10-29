package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.building.model.BuildingFloor;
import com.flexicore.building.model.Room;
import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.interfaces.dynamic.IdRefFieldInfo;
import com.flexicore.iot.ExternalServer;
import com.flexicore.model.territories.Address;
import com.flexicore.product.model.Gateway;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class EquipmentCreate extends ProductCreate {

	@FieldInfo(displayName = "latitude", description = "gateway latitude")
	private Double lat;
	@FieldInfo(displayName = "longitude", description = "gateway longitude")
	private Double lon;

	@FieldInfo(displayName = "x", description = "x latitude")
	private Double x;
	@FieldInfo(displayName = "y", description = "y longitude")
	private Double y;

	@FieldInfo(description = "warranty Expiration")
	private OffsetDateTime warrantyExpiration;

	private Boolean enable;

	@IdRefFieldInfo(description = "Communication Gateway used to connect to equipment", displayName = "Communication Gateway", refType = Gateway.class, list = false)
	private String communicationGatewayId;
	@JsonIgnore
	private Gateway gateway;

	@FieldInfo(description = "serial")
	private String serial;
	@FieldInfo
	private String externalId;
	@IdRefFieldInfo(refType = ExternalServer.class, list = false)
	private String externalServerId;
	@JsonIgnore
	private ExternalServer externalServer;

	@JsonIgnore
	private Address address;
	private String addressId;

	private String descriptor3D;
	@IdRefFieldInfo(refType = Room.class,list = false)
	private String roomId;
	@JsonIgnore
	private Room room;
	@IdRefFieldInfo(refType = BuildingFloor.class,list = false)
	private String buildingFloorId;
	@JsonIgnore
	private BuildingFloor buildingFloor;

	public String getExternalServerId() {
		return externalServerId;
	}

	public <T extends EquipmentCreate> T setExternalServerId(
			String externalServerId) {
		this.externalServerId = externalServerId;
		return (T) this;
	}

	public Double getLat() {
		return lat;
	}

	public <T extends EquipmentCreate> T setLat(Double lat) {
		this.lat = lat;
		return (T) this;
	}

	public Double getLon() {
		return lon;
	}

	public <T extends EquipmentCreate> T setLon(Double lon) {
		this.lon = lon;
		return (T) this;
	}

	public OffsetDateTime getWarrantyExpiration() {
		return warrantyExpiration;
	}

	public <T extends EquipmentCreate> T setWarrantyExpiration(
			OffsetDateTime warrantyExpiration) {
		this.warrantyExpiration = warrantyExpiration;
		return (T) this;
	}

	public Boolean getEnable() {
		return enable;
	}

	public <T extends EquipmentCreate> T setEnable(Boolean enable) {
		this.enable = enable;
		return (T) this;
	}

	public String getCommunicationGatewayId() {
		return communicationGatewayId;
	}

	public <T extends EquipmentCreate> T setCommunicationGatewayId(
			String communicationGatewayId) {
		this.communicationGatewayId = communicationGatewayId;
		return (T) this;
	}

	@JsonIgnore
	public Gateway getGateway() {
		return gateway;
	}

	public <T extends EquipmentCreate> T setGateway(Gateway gateway) {
		this.gateway = gateway;
		return (T) this;
	}

	public String getSerial() {
		return serial;
	}

	public <T extends EquipmentCreate> T setSerial(String serial) {
		this.serial = serial;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends EquipmentCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	public ExternalServer getExternalServer() {
		return externalServer;
	}

	public <T extends EquipmentCreate> T setExternalServer(
			ExternalServer externalServer) {
		this.externalServer = externalServer;
		return (T) this;
	}

	public Address getAddress() {
		return address;
	}

	public <T extends EquipmentCreate> T setAddress(Address address) {
		this.address = address;
		return (T) this;
	}

	public String getAddressId() {
		return addressId;
	}

	public <T extends EquipmentCreate> T setAddressId(String addressId) {
		this.addressId = addressId;
		return (T) this;
	}

	public Double getX() {
		return x;
	}

	public EquipmentCreate setX(Double x) {
		this.x = x;
		return this;
	}

	public Double getY() {
		return y;
	}

	public EquipmentCreate setY(Double y) {
		this.y = y;
		return this;
	}

	public String getDescriptor3D() {
		return descriptor3D;
	}

	public <T extends EquipmentCreate> T setDescriptor3D(String descriptor3D) {
		this.descriptor3D = descriptor3D;
		return (T) this;
	}

	public String getRoomId() {
		return roomId;
	}

	public <T extends EquipmentCreate> T setRoomId(String roomId) {
		this.roomId = roomId;
		return (T) this;
	}

	@JsonIgnore
	public Room getRoom() {
		return room;
	}

	public <T extends EquipmentCreate> T setRoom(Room room) {
		this.room = room;
		return (T) this;
	}

	public String getBuildingFloorId() {
		return buildingFloorId;
	}

	public <T extends EquipmentCreate> T setBuildingFloorId(String buildingFloorId) {
		this.buildingFloorId = buildingFloorId;
		return (T) this;
	}

	@JsonIgnore
	public BuildingFloor getBuildingFloor() {
		return buildingFloor;
	}

	public <T extends EquipmentCreate> T setBuildingFloor(BuildingFloor buildingFloor) {
		this.buildingFloor = buildingFloor;
		return (T) this;
	}
}
