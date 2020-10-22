package com.flexicore.product.interfaces;

import com.flexicore.building.model.BuildingFloor;
import com.flexicore.building.model.BuildingFloor_;
import com.flexicore.building.model.Room;
import com.flexicore.building.model.Room_;
import com.flexicore.data.jsoncontainers.SortingOrder;
import com.flexicore.interfaces.PluginRepository;
import com.flexicore.iot.ExternalServer;
import com.flexicore.iot.ExternalServer_;
import com.flexicore.model.Baselink_;
import com.flexicore.model.SortParameter;
import com.flexicore.model.territories.*;
import com.flexicore.product.containers.response.EquipmentGroupHolder;
import com.flexicore.product.model.*;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IEquipmentRepository extends PluginRepository {

	static <T extends Equipment> void addEquipmentFiltering(
			EquipmentFiltering filtering, CriteriaBuilder cb, Root<T> r,
			List<Predicate> preds) {
		Set<String> ids;
		if (filtering.getEquipmentIds() != null
				&& !(ids = filtering.getEquipmentIds().parallelStream()
						.filter(f -> f.getId() != null).map(f -> f.getId())
						.collect(Collectors.toSet())).isEmpty()) {

			Predicate pred = r.get(Equipment_.id).in(ids);
			preds.add(pred);
		}
		if (filtering.getEquipmentIds() != null
				&& !filtering.getEquipmentGroups().isEmpty()) {
			Join<T, EquipmentToGroup> join = r
					.join(Equipment_.equipmentToGroupList);
			Predicate pred = join.get(Baselink_.rightside).in(
					filtering.getEquipmentGroups());
			preds.add(pred);
		}
		if (filtering.getLocationArea() != null
				&& filtering.getLocationArea() != null) {
			Predicate predicate = cb.between(r.get(Equipment_.lat), filtering
					.getLocationArea().getLatStart(), filtering
					.getLocationArea().getLatEnd());
			predicate = cb.and(predicate, cb.between(r.get(Equipment_.lon),
					filtering.getLocationArea().getLonStart(), filtering
							.getLocationArea().getLonEnd()));
			preds.add(predicate);
		}
		if (filtering.getProductType() != null) {
			Predicate predicate = cb.equal(r.get(Equipment_.productType),
					filtering.getProductType());
			preds.add(predicate);
		}

		if (filtering.getExternalServers() != null && !filtering.getExternalServers().isEmpty()) {
			Set<String> externalServersIds = filtering.getExternalServers().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, ExternalServer> externalServerJoin = r.join(Equipment_.externalServer);
			Predicate predicate = externalServerJoin.get(ExternalServer_.id).in(externalServersIds);
			preds.add(predicate);
		}
		if (filtering.getProductStatusList() != null
				&& !filtering.getProductStatusList().isEmpty()) {
			Join<T, ProductToStatus> join = r
					.join(Equipment_.productToStatusList);
			Predicate pred = cb.and(
					join.get(Baselink_.rightside).in(
							filtering.getProductStatusList()),
					cb.isTrue(join.get(ProductToStatus_.enabled)));
			preds.add(pred);
		}

		if (filtering.getTypesToReturn() != null
				&& !filtering.getTypesToReturn().isEmpty()) {
			Predicate pred = r.get("dtype").in(
					filtering.getTypesToReturn().parallelStream()
							.map(f -> f.getSimpleName())
							.collect(Collectors.toSet()));
			preds.add(pred);
		}

		if (filtering.getGateways() != null
				&& !filtering.getGateways().isEmpty()) {
			Join<T, Gateway> join = r.join(Equipment_.communicationGateway);
			Set<String> gids = filtering.getGateways().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Predicate pred = join.get(Gateway_.id).in(gids);
			preds.add(pred);
		}
		if (filtering.getNeighbourhoods() != null
				&& !filtering.getNeighbourhoods().isEmpty()) {
			Join<T, Address> join = r.join(Equipment_.address);
			Join<Address, Neighbourhood> join1 = join
					.join(Address_.neighbourhood);

			Set<String> gids = filtering.getNeighbourhoods().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Predicate pred = join1.get(Neighbourhood_.id).in(gids);
			preds.add(pred);
		}
		if (filtering.getStreets() != null && !filtering.getStreets().isEmpty()) {
			Join<T, Address> join = r.join(Equipment_.address);
			Join<Address, Street> join1 = join.join(Address_.street);

			Set<String> gids = filtering.getStreets().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Predicate pred = join1.get(Street_.id).in(gids);
			preds.add(pred);
		}
		if (filtering.getExternalEquipmentIds() != null
				&& !filtering.getExternalEquipmentIds().isEmpty()) {
			preds.add(r.get(Equipment_.externalId).in(
					filtering.getExternalEquipmentIds().parallelStream()
							.map(f -> f.getId() + "")
							.collect(Collectors.toSet())));

		}

		if (filtering.getExcludeZeroLocation()) {
			preds.add(cb.and(cb.notEqual(r.get(Equipment_.lat), 0),
					cb.notEqual(r.get(Equipment_.lon), 0)));
		}

		if (filtering.getBuildingFloors() != null && !filtering.getBuildingFloors().isEmpty()) {
			Set<String> buildingFloorIds = filtering.getBuildingFloors().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, BuildingFloor> externalServerJoin = r.join(Equipment_.buildingFloor);
			Predicate predicate = externalServerJoin.get(BuildingFloor_.id).in(buildingFloorIds);
			preds.add(predicate);
		}

		if (filtering.getRooms() != null && !filtering.getRooms().isEmpty()) {
			Set<String> roomIds = filtering.getRooms().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
			Join<T, Room> externalServerJoin = r.join(Equipment_.room);
			Predicate predicate = externalServerJoin.get(Room_.id).in(roomIds);
			preds.add(predicate);
		}

	}

	static <T extends Equipment> String addEquipmentGeoHashFiltering(
			EquipmentGroupFiltering filtering, CriteriaBuilder cb, Root<T> r,
			List<Predicate> preds) {
		IEquipmentRepository.addEquipmentFiltering(filtering, cb, r, preds);

		String geoHashField = "geoHash" + filtering.getPrecision();
		List<SortParameter> sort = new ArrayList<>();
		sort.add(new SortParameter(geoHashField, SortingOrder.ASCENDING));
		filtering.setSort(sort);
		return geoHashField;
	}

	<T extends Equipment> List<T> getAllEquipments(Class<T> c,
			EquipmentFiltering filtering, SecurityContext securityContext);

	<T extends Equipment> long countAllEquipments(Class<T> c,
			EquipmentFiltering filtering, SecurityContext securityContext);

	<T extends Equipment> List<EquipmentGroupHolder> getAllEquipmentsGrouped(
			Class<T> c, EquipmentGroupFiltering filtering,
			SecurityContext securityContext);

	List<ProductToStatus> getStatusLinks(Set<String> equipmentIds);

	List<ProductToStatus> getCurrentStatusLinks(Set<String> equipmentIds);

	List<ProductTypeToProductStatus> getAllProductTypeToStatusLinks(
			Set<String> statusIds);
}
