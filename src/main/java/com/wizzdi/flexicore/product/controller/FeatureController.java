package com.wizzdi.flexicore.product.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.model.Feature;
import com.wizzdi.flexicore.product.model.Feature_;
import com.wizzdi.flexicore.product.request.FeatureCreate;
import com.wizzdi.flexicore.product.request.FeatureFilter;
import com.wizzdi.flexicore.product.request.FeatureUpdate;
import com.wizzdi.flexicore.product.service.FeatureService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Feature")

@Tag(name = "Feature")
@Extension
@RestController
public class FeatureController implements Plugin {

    @Autowired
    private FeatureService service;


    @Operation(summary = "getAllFeatures", description = "Lists all Feature")
    @IOperation(Name = "getAllFeatures", Description = "Lists all Feature")
    @PostMapping("/getAllFeatures")
    public PaginationResponse<Feature> getAllFeatures(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody FeatureFilter featureFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(featureFilter, securityContext);
        return service.getAllFeatures(securityContext, featureFilter);
    }


    @PostMapping("/createFeature")
    @Operation(summary = "createFeature", description = "Creates Feature")
    @IOperation(Name = "createFeature", Description = "Creates Feature")
    public Feature createFeature(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody FeatureCreate featureCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(featureCreate, securityContext);

        return service.createFeature(featureCreate, securityContext);
    }


    @PutMapping("/updateFeature")
    @Operation(summary = "updateFeature", description = "Updates Feature")
    @IOperation(Name = "updateFeature", Description = "Updates Feature")
    public Feature updateFeature(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody FeatureUpdate featureUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(featureUpdate, securityContext);
        Feature feature = service.getByIdOrNull(featureUpdate.getId(),
                Feature.class, Feature_.security, securityContext);
        if (feature == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Feature with id "
                    + featureUpdate.getId());
        }
        featureUpdate.setFeature(feature);

        return service.updateFeature(featureUpdate, securityContext);
    }
}