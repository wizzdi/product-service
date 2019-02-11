package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.request.ManufacturerCreate;
import com.flexicore.product.request.ManufacturerFiltering;
import com.flexicore.product.request.ManufacturerUpdate;
import com.flexicore.security.SecurityContext;

public interface IManufacturerService extends ServicePlugin {
    PaginationResponse<Manufacturer> getAllManufacturers(ManufacturerFiltering filtering, SecurityContext securityContext);

    Manufacturer createManufacturer(ManufacturerCreate manufacturerCreate, SecurityContext securityContext);

    Manufacturer createManufacturerNoMerge(ManufacturerCreate manufacturerCreate, SecurityContext securityContext);

    boolean updateManufacturerNoMerge(Manufacturer manufacturer, ManufacturerCreate manufacturerCreate);

    void validateManufacturerFiltering(ManufacturerFiltering filtering, SecurityContext securityContext);

    Manufacturer updateManufacturer(ManufacturerUpdate manufacturerUpdate, SecurityContext securityContext);

    void validate(ManufacturerCreate manufacturerCreate, SecurityContext securityContext);
}
