package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.CreatePermissionGroupRequest;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.model.PermissionGroup;
import com.flexicore.product.containers.request.GroupCreate;
import com.flexicore.product.containers.request.GroupUpdate;
import com.flexicore.product.data.EquipmentGroupRepository;
import com.flexicore.product.interfaces.IGroupService;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.EquipmentToGroup;
import com.flexicore.product.model.GroupFiltering;
import com.flexicore.product.request.EquipmentToGroupFiltering;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.PermissionGroupService;

import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class GroupService implements IGroupService {

	@PluginInfo(version = 1)
	@Autowired
	private EquipmentGroupRepository equipmentRepository;

	@Autowired
	private PermissionGroupService permissionGroupService;

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids,
			SecurityContext securityContext) {
		return equipmentRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return equipmentRepository.getByIdOrNull(id, c, batchString,
				securityContext);
	}

	@Override
	public PaginationResponse<EquipmentGroup> getAllEquipmentGroups(
			GroupFiltering filtering, SecurityContext securityContext) {
		List<EquipmentGroup> list = listAllEquipmentGroups(filtering,
				securityContext);
		long count = equipmentRepository.countAllEquipmentGroups(filtering,
				securityContext);
		return new PaginationResponse<>(list, filtering, count);
	}

	@Override
	public List<EquipmentGroup> listAllEquipmentGroups(
			GroupFiltering filtering, SecurityContext securityContext) {
		return equipmentRepository.getAllEquipmentGroups(filtering,
				securityContext);
	}

	@Override
	public List<EquipmentToGroup> getEquipmentToGroup(Set<String> equipmentIds) {
		return equipmentRepository.getEquipmentToGroup(
				new EquipmentToGroupFiltering().setEquipmentIds(equipmentIds)
						.setRaw(true), null);

	}

	@Override
	public List<EquipmentToGroup> listAllEquipmentToGroup(
			EquipmentToGroupFiltering equipmentToGroupFiltering,
			SecurityContext securityContext) {
		return equipmentRepository.getEquipmentToGroup(
				equipmentToGroupFiltering, securityContext);

	}

	public EquipmentGroup getRootEquipmentGroup(SecurityContext securityContext) {
		return equipmentRepository.getRootEquipmentGroup(securityContext);
	}

	public EquipmentGroup createGroup(GroupCreate groupCreate,
			SecurityContext securityContext) {
		List<Object> toMerge = new ArrayList<>();
		EquipmentGroup equipmentGroup = EquipmentGroup.s().CreateUnchecked(
				groupCreate.getName(), securityContext);

		equipmentGroup.Init();
		equipmentGroup.setDescription(groupCreate.getDescription());
		equipmentGroup.setParent(groupCreate.getParent());
		PermissionGroup permissionGroup = permissionGroupService
				.createPermissionGroupNoMerge(
						new CreatePermissionGroupRequest().setDescription(
								groupCreate.getDescription()).setName(
								groupCreate.getName()), securityContext);
		equipmentGroup.setRelatedPermissionGroup(permissionGroup);
		toMerge.add(permissionGroup);
		toMerge.add(equipmentGroup);
		equipmentRepository.massMerge(toMerge);
		return equipmentGroup;
	}

	public EquipmentGroup updateGroup(GroupUpdate groupUpdate,
			SecurityContext securityContext) {
		EquipmentGroup equipmentGroup = groupUpdate.getEquipmentGroup();
		boolean update = false;
		if (groupUpdate.getName() != null
				&& !groupUpdate.getName().equals(equipmentGroup.getName())) {
			equipmentGroup.setName(groupUpdate.getName());
			update = true;
		}

		if (groupUpdate.getDescription() != null
				&& !groupUpdate.getDescription().equals(
						equipmentGroup.getDescription())) {
			equipmentGroup.setDescription(groupUpdate.getDescription());
			update = true;
		}

		if (groupUpdate.getParent() != null
				&& equipmentGroup.getParent() != null
				&& !groupUpdate.getParent().getId()
						.equals(equipmentGroup.getParent().getId())) {
			equipmentGroup.setParent(groupUpdate.getParent());
			update = true;
		}
		if (update) {
			equipmentRepository.merge(equipmentGroup);
		}
		return equipmentGroup;
	}

	@Override
	public void validateGroupFiltering(GroupFiltering filtering,
			SecurityContext securityContext) {
		Set<String> ids = filtering.getGroupIds().parallelStream()
				.map(f -> f.getId()).collect(Collectors.toSet());
		List<EquipmentGroup> groups = filtering.getGroupIds().isEmpty()
				? new ArrayList<>()
				: listByIds(EquipmentGroup.class, ids, securityContext);
		ids.removeAll(groups.parallelStream().map(f -> f.getId())
				.collect(Collectors.toSet()));
		if (!ids.isEmpty()) {
			throw new BadRequestException("could not find groups with ids "
					+ ids.parallelStream().collect(Collectors.joining(",")));
		}
		filtering.setEquipmentGroups(groups);
		Set<String> equipmentIds = filtering.getEquipmentIdFilterings()
				.parallelStream().map(f -> f.getId())
				.collect(Collectors.toSet());
		List<Equipment> equipment = !filtering.getEquipmentIdFilterings()
				.isEmpty() ? listByIds(Equipment.class, equipmentIds,
				securityContext) : new ArrayList<>();
		equipmentIds.removeAll(equipment.parallelStream().map(f -> f.getId())
				.collect(Collectors.toSet()));
		if (!equipmentIds.isEmpty()) {
			throw new BadRequestException("No Equipments with ids "
					+ equipmentIds);
		}
		filtering.setEquipment(equipment);
	}
}
