package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.BuildingFloor;
import com.flexicore.product.model.BuildingFloorFiltering;
import com.flexicore.product.request.BuildingFloorCreate;
import com.flexicore.product.request.BuildingFloorUpdate;
import com.flexicore.security.SecurityContext;

import java.util.List;

public interface IBuildingFloorService extends ServicePlugin {
    void validate(BuildingFloorFiltering filtering, SecurityContext securityContext);

    PaginationResponse<BuildingFloor> getAllBuildingFloors(BuildingFloorFiltering filtering, SecurityContext securityContext);

    List<BuildingFloor> listAllBuildingFloors(BuildingFloorFiltering filtering, SecurityContext securityContext);

    void validateCreate(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext);

    void validateUpdate(BuildingFloorUpdate buildingFloorCreate, SecurityContext securityContext);

    BuildingFloor createBuildingFloor(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext);

    BuildingFloor createBuildingFloorNoMerge(BuildingFloorCreate buildingFloorCreate, SecurityContext securityContext);

    boolean updateBuildingFloorNoMerge(BuildingFloorCreate buildingFloorCreate, BuildingFloor buildingFloor);

    BuildingFloor updateBuildingFloor(BuildingFloorUpdate buildingFloorUpdate, SecurityContext securityContext);
}
