package com.wizzdi.flexicore.product.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;

import com.wizzdi.flexicore.product.model.Manufacturer;
import com.wizzdi.flexicore.product.model.Manufacturer_;
import com.wizzdi.flexicore.product.request.ManufacturerCreate;
import com.wizzdi.flexicore.product.request.ManufacturerFilter;
import com.wizzdi.flexicore.product.request.ManufacturerUpdate;
import com.wizzdi.flexicore.product.service.ManufacturerService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Manufacturer")

@Tag(name = "Manufacturer")
@Extension
@RestController
public class ManufacturerController implements Plugin {

    @Autowired
    private ManufacturerService service;


    @Operation(summary = "getAllManufacturers", description = "Lists all Manufacturer")
    @IOperation(Name = "getAllManufacturers", Description = "Lists all Manufacturer")
    @PostMapping("/getAllManufacturers")
    public PaginationResponse<Manufacturer> getAllManufacturers(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ManufacturerFilter manufacturerFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(manufacturerFilter, securityContext);
        return service.getAllManufacturers(securityContext, manufacturerFilter);
    }


    @PostMapping("/createManufacturer")
    @Operation(summary = "createManufacturer", description = "Creates Manufacturer")
    @IOperation(Name = "createManufacturer", Description = "Creates Manufacturer")
    public Manufacturer createManufacturer(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ManufacturerCreate manufacturerCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(manufacturerCreate, securityContext);

        return service.createManufacturer(manufacturerCreate, securityContext);
    }


    @PutMapping("/updateManufacturer")
    @Operation(summary = "updateManufacturer", description = "Updates Manufacturer")
    @IOperation(Name = "updateManufacturer", Description = "Updates Manufacturer")
    public Manufacturer updateManufacturer(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ManufacturerUpdate manufacturerUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(manufacturerUpdate, securityContext);
        Manufacturer manufacturer = service.getByIdOrNull(manufacturerUpdate.getId(),
                Manufacturer.class, Manufacturer_.security, securityContext);
        if (manufacturer == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Manufacturer with id "
                    + manufacturerUpdate.getId());
        }
        manufacturerUpdate.setManufacturer(manufacturer);

        return service.updateManufacturer(manufacturerUpdate, securityContext);
    }
}