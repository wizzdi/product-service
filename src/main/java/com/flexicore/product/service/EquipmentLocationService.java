package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.data.EquipmentLocationNoSQLRepository;
import com.flexicore.product.messages.EquipmentLocationChanged;
import com.flexicore.product.model.Equipment;
import com.flexicore.product.model.EquipmentLocation;
import com.flexicore.product.request.EquipmentLocationCreate;
import com.flexicore.product.request.EquipmentLocationFiltering;
import com.flexicore.security.SecurityContext;
import com.flexicore.service.BaseclassNoSQLService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;


@PluginInfo(version = 1)
@Component
@Extension
public class EquipmentLocationService implements ServicePlugin {

    @Autowired
    @PluginInfo(version = 1)
    private EquipmentLocationNoSQLRepository equipmentLocationNoSQLRepository;
    @Autowired
    private BaseclassNoSQLService baseclassNoSQLService;

    @Autowired
    @PluginInfo(version = 1)
    private EquipmentService equipmentService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    public EquipmentLocation createEquipmentLocation(EquipmentLocationCreate equipmentLocationCreate) {
        EquipmentLocation equipmentLocation = createEquipmentLocationNoMerge(equipmentLocationCreate);
        equipmentLocationNoSQLRepository.merge(equipmentLocation);
        applicationEventPublisher.publishEvent(new EquipmentLocationChanged(equipmentLocation));
        return equipmentLocation;
    }

    private EquipmentLocation createEquipmentLocationNoMerge(EquipmentLocationCreate equipmentLocationCreate) {
        EquipmentLocation equipmentLocation = new EquipmentLocation();
        updateEquipmentLocationNoMerge(equipmentLocation, equipmentLocationCreate);
        return equipmentLocation;
    }

    private boolean updateEquipmentLocationNoMerge(EquipmentLocation equipmentLocation, EquipmentLocationCreate equipmentLocationCreate) {
        boolean update = baseclassNoSQLService.updateBaseclassNoSQLNoMerge(equipmentLocation, equipmentLocationCreate);

        if (equipmentLocationCreate.getBuildingFloorId() != null && !equipmentLocationCreate.getBuildingFloorId().equals(equipmentLocation.getBuildingFloorId())) {
            equipmentLocation.setBuildingFloorId(equipmentLocationCreate.getBuildingFloorId());
            update = true;
        }
        if (equipmentLocationCreate.getRoomId() != null && !equipmentLocationCreate.getRoomId().equals(equipmentLocation.getRoomId())) {
            equipmentLocation.setRoomId(equipmentLocationCreate.getRoomId());
            update = true;
        }

        if (equipmentLocationCreate.getEquipmentId() != null && !equipmentLocationCreate.getEquipmentId().equals(equipmentLocation.getEquipmentId())) {
            equipmentLocation.setEquipmentId(equipmentLocationCreate.getEquipmentId());
            update = true;
        }

        if (equipmentLocationCreate.getDateAtLocation() != null) {
            Date date = Date.from(equipmentLocationCreate.getDateAtLocation().toInstant());
            if (!date.equals(equipmentLocation.getDateAtLocation())) {
                equipmentLocation.setDateAtLocation(date);
                update = true;
            }

        }
        if (equipmentLocationCreate.getLat() != null && !equipmentLocationCreate.getLat().equals(equipmentLocation.getLat())) {
            equipmentLocation.setLat(equipmentLocationCreate.getLat());
            update = true;
        }
        if (equipmentLocationCreate.getLon() != null && !equipmentLocationCreate.getLon().equals(equipmentLocation.getLon())) {
            equipmentLocation.setLon(equipmentLocationCreate.getLon());
            update = true;
        }
        if (equipmentLocationCreate.getX() != null && !equipmentLocationCreate.getX().equals(equipmentLocation.getX())) {
            equipmentLocation.setX(equipmentLocationCreate.getX());
            update = true;
        }
        if (equipmentLocationCreate.getY() != null && !equipmentLocationCreate.getY().equals(equipmentLocation.getY())) {
            equipmentLocation.setY(equipmentLocationCreate.getY());
            update = true;
        }
        return update;
    }


    public void validateFiltering(EquipmentLocationFiltering equipmentLocationFiltering, SecurityContext securityContext) {
        Set<String> equipmentIds=equipmentLocationFiltering.getEquipmentIds().stream().map(f->f.getId()).collect(Collectors.toSet());
        Map<String, Equipment> equipmentMap=equipmentIds.isEmpty()?new HashMap<>():equipmentService.listByIds(Equipment.class,equipmentIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
        equipmentIds.removeAll(equipmentMap.keySet());
        if(!equipmentIds.isEmpty()){
            throw new BadRequestException("No Equipments with ids "+equipmentIds);
        }
        equipmentLocationFiltering.setEquipment(new ArrayList<>(equipmentMap.values()));
    }

    public PaginationResponse<EquipmentLocation> getAllEquipmentLocations(EquipmentLocationFiltering equipmentLocationFiltering) {
        List<EquipmentLocation> equipmentLocations=listAllEquipmentLocations(equipmentLocationFiltering);
        long count=equipmentLocationNoSQLRepository.countAllEquipmentLocation(equipmentLocationFiltering);
        return new PaginationResponse<>(equipmentLocations,equipmentLocationFiltering,count);
    }

    private List<EquipmentLocation> listAllEquipmentLocations(EquipmentLocationFiltering equipmentLocationFiltering) {

        return equipmentLocationNoSQLRepository.getAllEquipmentLocation(equipmentLocationFiltering);
    }
}
