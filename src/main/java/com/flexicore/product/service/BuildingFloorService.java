package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;
import com.flexicore.product.data.BuildingFloorRepository;
import com.flexicore.product.interfaces.IBuildingFloorService;
import com.flexicore.product.interfaces.IEquipmentService;
import com.flexicore.product.model.Building;
import com.flexicore.product.model.BuildingFloor;
import com.flexicore.product.model.BuildingFloorFiltering;
import com.flexicore.product.request.BuildingFloorCreate;
import com.flexicore.product.request.BuildingFloorUpdate;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class BuildingFloorService implements IBuildingFloorService {

    @Inject
    @PluginInfo(version = 1)
    private BuildingFloorRepository repository;

    @Inject
    @PluginInfo(version = 1)
    private IEquipmentService equipmentService;

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }

    @Override
    public void validate(BuildingFloorFiltering filtering, SecurityContext securityContext) {

        Set<String> buildingIds=filtering.getBuildingIdFilterings().parallelStream().map(f->f.getId()).collect(Collectors.toSet());
        Map<String, Building> buildingMap=buildingIds.isEmpty()?new HashMap<>():repository.listByIds(Building.class,buildingIds,securityContext).parallelStream().collect(Collectors.toMap(f->f.getId(),f->f));
        buildingIds.removeAll(buildingMap.keySet());
        if(!buildingIds.isEmpty()){
            throw new BadRequestException("No Buildings with ids "+buildingIds);
        }
        filtering.setBuildings(new ArrayList<>(buildingMap.values()));


    }

    @Override
    public PaginationResponse<BuildingFloor> getAllBuildingFloors(BuildingFloorFiltering filtering, SecurityContext securityContext) {
        List<BuildingFloor> list = listAllBuildingFloors(filtering, securityContext);
        long count = repository.countAllBuildingFloors(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, count);
    }

    @Override
    public List<BuildingFloor> listAllBuildingFloors(BuildingFloorFiltering filtering, SecurityContext securityContext) {
        return repository.listAllBuildingFloors(filtering, securityContext);
    }

    private void populate(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext) {
        String diagramId=buildingFloorCreate.getDiagramId();
        FileResource diagram=diagramId!=null?getByIdOrNull(diagramId,FileResource.class,null,securityContext):null;
        if(diagram==null&&diagramId!=null){
            throw new BadRequestException("No FileResource with id "+diagramId);
        }
        buildingFloorCreate.setDiagram(diagram);

        String buildingId=buildingFloorCreate.getBuildingId();
        Building building=buildingId!=null?getByIdOrNull(buildingId,Building.class,null,securityContext):null;
        if(building==null&&buildingId!=null){
            throw new BadRequestException("No Building with id "+buildingId);
        }
        buildingFloorCreate.setBuilding(building);
    }


    @Override
    public void validateCreate(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext) {
        populate(buildingFloorCreate, securityContext);
    }

    @Override
    public void validateUpdate(BuildingFloorUpdate buildingFloorCreate, SecurityContext securityContext) {
        populate(buildingFloorCreate, securityContext);
    }

    @Override
    public BuildingFloor createBuildingFloor(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext) {
        BuildingFloor buildingFloor = createBuildingFloorNoMerge(buildingFloorCreate, securityContext);
        repository.merge(buildingFloor);
        return buildingFloor;
    }

    @Override
    public BuildingFloor createBuildingFloorNoMerge(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext) {
        BuildingFloor buildingFloor = BuildingFloor.s().CreateUnchecked(buildingFloorCreate.getName(), securityContext);
        buildingFloor.Init();
        updateBuildingFloorNoMerge(buildingFloorCreate, buildingFloor);
        return buildingFloor;
    }

    @Override
    public boolean updateBuildingFloorNoMerge(BuildingFloorCreate buildingFloorCreate, BuildingFloor buildingFloor) {
        boolean update = false;
        if (buildingFloorCreate.getName() != null && !buildingFloorCreate.getName().equals(buildingFloor.getName())) {
            buildingFloor.setName(buildingFloorCreate.getName());
            update = true;
        }

        if (buildingFloorCreate.getDescription() != null && !buildingFloorCreate.getDescription().equals(buildingFloor.getDescription())) {
            buildingFloor.setDescription(buildingFloorCreate.getDescription());
            update = true;
        }

        if (buildingFloorCreate.getFloorNumber() != null && buildingFloorCreate.getFloorNumber()!=buildingFloor.getFloorNumber()) {
            buildingFloor.setFloorNumber(buildingFloorCreate.getFloorNumber());
            update = true;
        }

        if (buildingFloorCreate.getDiagram() != null && (buildingFloor.getDiagram()==null||!buildingFloorCreate.getDiagram().getId().equals(buildingFloor.getDiagram().getId())) ){
            buildingFloor.setDiagram(buildingFloorCreate.getDiagram());
            update = true;
        }

        if (buildingFloorCreate.getBuilding() != null && (buildingFloor.getBuilding()==null||!buildingFloorCreate.getBuilding().getId().equals(buildingFloor.getBuilding().getId())) ){
            buildingFloor.setBuilding(buildingFloorCreate.getBuilding());
            update = true;
        }
        return update;

    }

    @Override
    public BuildingFloor updateBuildingFloor(BuildingFloorUpdate buildingFloorUpdate, SecurityContext securityContext) {
        BuildingFloor buildingFloor=buildingFloorUpdate.getBuildingFloor();
        if(updateBuildingFloorNoMerge(buildingFloorUpdate,buildingFloor)){
            repository.merge(buildingFloor);
        }
        return buildingFloor;
    }
}
