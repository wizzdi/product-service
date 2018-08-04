package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.containers.request.GroupCreate;
import com.flexicore.product.containers.request.GroupFiltering;
import com.flexicore.product.containers.request.GroupUpdate;
import com.flexicore.product.data.EquipmentGroupRepository;
import com.flexicore.product.data.EquipmentRepository;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.SecurityService;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

@PluginInfo(version = 1)
public class GroupService implements ServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentGroupRepository equipmentRepository;


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return equipmentRepository.getByIdOrNull(id, c, batchString, securityContext);
    }


    public PaginationResponse<EquipmentGroup> getAllEquipmentGroups(GroupFiltering filtering, SecurityContext securityContext) {
        List<EquipmentGroup> list= equipmentRepository.getAllEquipmentGroups(filtering,securityContext);
        long count=equipmentRepository.countAllEquipmentGroups(filtering,securityContext);
        return new PaginationResponse<>(list,filtering,count);
    }

    public EquipmentGroup getRootEquipmentGroup(SecurityContext securityContext) {
        return equipmentRepository.getRootEquipmentGroup(securityContext);
    }

    public EquipmentGroup createGroup(GroupCreate groupCreate, SecurityContext securityContext) {
        EquipmentGroup equipmentGroup=EquipmentGroup.s().CreateUnchecked(groupCreate.getName(),securityContext.getUser());
        equipmentGroup.Init();
        equipmentGroup.setDescription(groupCreate.getDescription());
        equipmentGroup.setParent(groupCreate.getParent());
        equipmentRepository.merge(equipmentGroup);
        return equipmentGroup;
    }

    public EquipmentGroup updateGroup(GroupUpdate groupUpdate, SecurityContext securityContext) {
        EquipmentGroup equipmentGroup=groupUpdate.getEquipmentGroup();
        boolean update=false;
        if(groupUpdate.getName()!=null &&! groupUpdate.getName().equals(equipmentGroup.getName())){
            equipmentGroup.setName(groupUpdate.getName());
            update=true;
        }

        if(groupUpdate.getDescription()!=null &&! groupUpdate.getDescription().equals(equipmentGroup.getDescription())){
            equipmentGroup.setDescription(groupUpdate.getDescription());
            update=true;
        }

        if(groupUpdate.getParent()!=null &&equipmentGroup.getParent()!=null&&
                ! groupUpdate.getParent().getId().equals(equipmentGroup.getParent().getId())){
            equipmentGroup.setParent(groupUpdate.getParent());
            update=true;
        }
        if(update){
            equipmentRepository.merge(equipmentGroup);
        }
        return equipmentGroup;
    }
}
