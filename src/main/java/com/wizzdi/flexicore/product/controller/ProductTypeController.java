package com.wizzdi.flexicore.product.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.model.ProductType;
import com.wizzdi.flexicore.product.model.ProductType_;
import com.wizzdi.flexicore.product.request.ProductTypeCreate;
import com.wizzdi.flexicore.product.request.ProductTypeFilter;
import com.wizzdi.flexicore.product.request.ProductTypeUpdate;
import com.wizzdi.flexicore.product.service.ProductTypeService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/ProductType")

@Tag(name = "ProductType")
@Extension
@RestController
public class ProductTypeController implements Plugin {

    @Autowired
    private ProductTypeService service;


    @Operation(summary = "getAllProductTypes", description = "Lists all ProductType")
    @IOperation(Name = "getAllProductTypes", Description = "Lists all ProductType")
    @PostMapping("/getAllProductTypes")
    public PaginationResponse<ProductType> getAllProductTypes(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ProductTypeFilter productTypeFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(productTypeFilter, securityContext);
        return service.getAllProductTypes(securityContext, productTypeFilter);
    }


    @PostMapping("/createProductType")
    @Operation(summary = "createProductType", description = "Creates ProductType")
    @IOperation(Name = "createProductType", Description = "Creates ProductType")
    public ProductType createProductType(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ProductTypeCreate productTypeCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(productTypeCreate, securityContext);

        return service.createProductType(productTypeCreate, securityContext);
    }


    @PutMapping("/updateProductType")
    @Operation(summary = "updateProductType", description = "Updates ProductType")
    @IOperation(Name = "updateProductType", Description = "Updates ProductType")
    public ProductType updateProductType(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ProductTypeUpdate productTypeUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(productTypeUpdate, securityContext);
        ProductType productType = service.getByIdOrNull(productTypeUpdate.getId(),
                ProductType.class, ProductType_.security, securityContext);
        if (productType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no ProductType with id "
                    + productTypeUpdate.getId());
        }
        productTypeUpdate.setProductType(productType);

        return service.updateProductType(productTypeUpdate, securityContext);
    }
}