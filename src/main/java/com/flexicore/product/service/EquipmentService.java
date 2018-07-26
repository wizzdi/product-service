package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.product.containers.request.EquipmentCreate;
import com.flexicore.product.containers.request.EquipmentFiltering;
import com.flexicore.product.containers.request.EquipmentUpdate;
import com.flexicore.product.containers.request.LinkToGroup;
import com.flexicore.product.data.EquipmentRepository;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentGroup;
import com.flexicore.product.model.EquipmentToGroup;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaselinkService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@PluginInfo(version = 1)
public class EquipmentService implements ServicePlugin {

    @Inject
    @PluginInfo(version = 1)
    private EquipmentRepository equipmentRepository;

    @Inject
    @PluginInfo(version = 1)
    private BaselinkService baselinkService;



    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return equipmentRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return equipmentRepository.getByIdOrNull(id, c, batchString, securityContext);
    }

    public <T extends Equipment> List<T> getAllEquipments(Class<T> c,EquipmentFiltering filtering, SecurityContext securityContext) {
        return equipmentRepository.getAllEquipments(c,filtering,securityContext);
    }

    public Equipment createEquipment(EquipmentCreate equipmentCreate, SecurityContext securityContext) {
        Equipment equipment=Equipment.s().CreateUnchecked(equipmentCreate.getName(),securityContext.getUser());
        equipment.Init();
        updateEquipmentNoMerge(equipmentCreate,equipment);
        equipmentRepository.merge(equipment);
        return equipment;
    }

    public EquipmentToGroup createEquipmentToGroup(LinkToGroup linkToGroup, SecurityContext securityContext){
       return baselinkService.linkEntities(linkToGroup.getEquipment(),linkToGroup.getEquipmentGroup(),EquipmentToGroup.class);

    }



    public boolean updateEquipmentNoMerge(EquipmentCreate equipmentCreate,Equipment equipment){
        boolean update=false;
        if(equipmentCreate.getName()!=null &&! equipmentCreate.getName().equals(equipment.getName())){
            equipment.setName(equipmentCreate.getName());
            update=true;
        }

        if(equipmentCreate.getDescription()!=null &&! equipmentCreate.getDescription().equals(equipment.getDescription())){
            equipment.setDescription(equipmentCreate.getDescription());
            update=true;
        }
        if(equipmentCreate.getWarrantyExpiration()!=null &&! equipmentCreate.getWarrantyExpiration().equals(equipment.getWarrantyExpiration())){
            equipment.setWarrantyExpiration(equipmentCreate.getWarrantyExpiration());
            update=true;
        }

        if(equipmentCreate.getLat()!=null &&! equipmentCreate.getLat().equals(equipment.getLat())){
            equipment.setLat(equipmentCreate.getLat());
            update=true;
        }

        if(equipmentCreate.getLon()!=null &&! equipmentCreate.getLon().equals(equipment.getLon())){
            equipment.setLon(equipmentCreate.getLon());
            update=true;
        }
        if(equipmentCreate.getSerial()!=null &&! equipmentCreate.getSerial().equals(equipment.getSerial())){
            equipment.setSerial(equipmentCreate.getSerial());
            update=true;
        }
        return update;

    }

    public Equipment updateEquipment(EquipmentUpdate equipmentUpdate, SecurityContext securityContext) {
        if(updateEquipmentNoMerge(equipmentUpdate,equipmentUpdate.getEquipment())){
            equipmentRepository.merge(equipmentUpdate.getEquipment());
        }
        return equipmentUpdate.getEquipment();
    }
}
