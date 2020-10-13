package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.CreatePermissionGroupRequest;
import com.flexicore.data.jsoncontainers.PaginationResponse;
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
import com.flexicore.service.BaseclassNewService;
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

	@Autowired
	private BaseclassNewService baseclassNewService;

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
		EquipmentGroup equipmentGroup = createGroupNoMerge(groupCreate, securityContext);
		CreatePermissionGroupRequest createPermissionGroupRequest = new CreatePermissionGroupRequest()
				.setExternalId(groupCreate.getExternalId())
				.setDescription(groupCreate.getDescription())
				.setName(groupCreate.getName());
		PermissionGroup permissionGroup = permissionGroupService
				.createPermissionGroupNoMerge(
						createPermissionGroupRequest, securityContext);
		equipmentGroup.setRelatedPermissionGroup(permissionGroup);
		toMerge.add(permissionGroup);
		toMerge.add(equipmentGroup);
		equipmentRepository.massMerge(toMerge);
		return equipmentGroup;
	}

	public EquipmentGroup createGroupNoMerge(GroupCreate groupCreate, SecurityContext securityContext) {
		EquipmentGroup equipmentGroup = new EquipmentGroup(groupCreate.getName(), securityContext);
		updateGroupNoMerge(groupCreate,equipmentGroup);
		return equipmentGroup;
	}

	public EquipmentGroup updateGroup(GroupUpdate groupUpdate,
											 SecurityContext securityContext) {
		EquipmentGroup equipmentGroup=groupUpdate.getEquipmentGroup();
		if(updateGroupNoMerge(groupUpdate,equipmentGroup)){
			equipmentRepository.merge(equipmentGroup);
		}
		return equipmentGroup;

	}

	public boolean updateGroupNoMerge(GroupCreate groupCreate, EquipmentGroup equipmentGroup) {

		boolean update = baseclassNewService.updateBaseclassNoMerge(groupCreate,equipmentGroup);
		if (groupCreate.getExternalId() != null && !groupCreate.getExternalId().equals(equipmentGroup.getExternalId())) {
			equipmentGroup.setExternalId(groupCreate.getExternalId());
			update = true;
		}

		if (groupCreate.getParent() != null && equipmentGroup.getParent() != null && !groupCreate.getParent().getId().equals(equipmentGroup.getParent().getId())) {
			equipmentGroup.setParent(groupCreate.getParent());
			update = true;
		}
		return update;
	}

	@Override
	public void validateGroupFiltering(GroupFiltering filtering,
			SecurityContext securityContext) {
		Set<String> ids = filtering.getGroupIds().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
		List<EquipmentGroup> groups = filtering.getGroupIds().isEmpty() ? new ArrayList<>() : listByIds(EquipmentGroup.class, ids, securityContext);
		ids.removeAll(groups.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
		if (!ids.isEmpty()) {
			throw new BadRequestException("could not find groups with ids " + ids.parallelStream().collect(Collectors.joining(",")));
		}
		filtering.setEquipmentGroups(groups);

		Set<String> equipmentIds = filtering.getEquipmentIdFilterings().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
		List<Equipment> equipment = !filtering.getEquipmentIdFilterings().isEmpty() ? listByIds(Equipment.class, equipmentIds, securityContext) : new ArrayList<>();
		equipmentIds.removeAll(equipment.parallelStream().map(f -> f.getId()).collect(Collectors.toSet()));
		if (!equipmentIds.isEmpty()) {
			throw new BadRequestException("No Equipments with ids " + equipmentIds);
		}
		filtering.setEquipment(equipment);

	}
}
