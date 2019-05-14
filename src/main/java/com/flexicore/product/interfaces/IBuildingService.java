package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.Building;
import com.flexicore.product.model.BuildingFiltering;

import com.flexicore.product.request.BuildingCreate;
import com.flexicore.product.request.BuildingUpdate;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IBuildingService extends ServicePlugin {
    void validate(BuildingFiltering filtering, SecurityContext securityContext);

    PaginationResponse<Building> getAllBuildings(BuildingFiltering filtering, SecurityContext securityContext);

    List<Building> listAllBuildings(BuildingFiltering filtering, SecurityContext securityContext);

    void validateCreate(BuildingCreate buildingCreate, SecurityContext securityContext);

    void validateUpdate(BuildingUpdate buildingCreate, SecurityContext securityContext);

    Building createBuilding(BuildingCreate buildingCreate, SecurityContext securityContext);

    Building createBuildingNoMerge(BuildingCreate buildingCreate, SecurityContext securityContext);

    boolean updateBuildingNoMerge(BuildingCreate buildingCreate, Building building);

    Building updateBuilding(BuildingUpdate buildingUpdate, SecurityContext securityContext);
}
