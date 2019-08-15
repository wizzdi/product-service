package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.product.data.BuildingRepository;
import com.flexicore.product.interfaces.IBuildingService;
import com.flexicore.product.interfaces.IEquipmentService;
import com.flexicore.product.model.Building;
import com.flexicore.product.model.BuildingFiltering;
import com.flexicore.product.request.BuildingCreate;
import com.flexicore.product.request.BuildingUpdate;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import java.util.List;

@PluginInfo(version = 1)
public class BuildingService implements IBuildingService {

    @Inject
    @PluginInfo(version = 1)
    private BuildingRepository repository;

    @Inject
    @PluginInfo(version = 1)
    private IEquipmentService equipmentService;

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return repository.getByIdOrNull(id, c, batchString, securityContext);
    }

    @Override
    public void validate(BuildingFiltering filtering, SecurityContext securityContext) {

        equipmentService.validateFiltering(filtering,securityContext);

    }

    @Override
    public PaginationResponse<Building> getAllBuildings(BuildingFiltering filtering, SecurityContext securityContext) {
        List<Building> list = listAllBuildings(filtering, securityContext);
        long count = repository.countAllBuildings(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, count);
    }

    @Override
    public List<Building> listAllBuildings(BuildingFiltering filtering, SecurityContext securityContext) {
        return repository.listAllBuildings(filtering, securityContext);
    }

    public void populate(BuildingCreate buildingCreate, SecurityContext securityContext) {

    }


    @Override
    public void validateCreate(BuildingCreate buildingCreate, SecurityContext securityContext) {
      equipmentService.validateEquipmentCreate(buildingCreate,securityContext);
      buildingCreate.setProductType(equipmentService.getBuildingProductType());
    }

    @Override
    public void validateUpdate(BuildingUpdate buildingCreate, SecurityContext securityContext) {
      equipmentService.validateEquipmentCreate(buildingCreate,securityContext);
    }

    @Override
    public Building createBuilding(BuildingCreate buildingCreate, SecurityContext securityContext) {
        Building building = createBuildingNoMerge(buildingCreate, securityContext);
        repository.merge(building);
        return building;
    }

    @Override
    public Building createBuildingNoMerge(BuildingCreate buildingCreate, SecurityContext securityContext) {
        Building building = Building.s().CreateUnchecked(buildingCreate.getName(), securityContext);
        building.Init();
        updateBuildingNoMerge(buildingCreate, building);
        return building;
    }

    @Override
    public boolean updateBuildingNoMerge(BuildingCreate buildingCreate, Building building) {
        boolean update = equipmentService.updateEquipmentNoMerge(buildingCreate,building);
        if (buildingCreate.getName() != null && !buildingCreate.getName().equals(building.getName())) {
            building.setName(buildingCreate.getName());
            update = true;
        }

        return update;

    }

    @Override
    public Building updateBuilding(BuildingUpdate buildingUpdate, SecurityContext securityContext) {
        Building building=buildingUpdate.getBuilding();
        if(updateBuildingNoMerge(buildingUpdate,building)){
            repository.merge(building);
        }
        return building;
    }
}
