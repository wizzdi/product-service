package com.wizzdi.flexicore.product.controller;

import com.flexicore.annotations.IOperation;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.product.model.Model;
import com.wizzdi.flexicore.product.model.Model_;
import com.wizzdi.flexicore.product.request.ModelCreate;
import com.wizzdi.flexicore.product.request.ModelFilter;
import com.wizzdi.flexicore.product.request.ModelUpdate;
import com.wizzdi.flexicore.product.service.ModelService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@OperationsInside

@RequestMapping("/plugins/Model")

@Tag(name = "Model")
@Extension
@RestController
public class ModelController implements Plugin {

    @Autowired
    private ModelService service;


    @Operation(summary = "getAllModels", description = "Lists all Model")
    @IOperation(Name = "getAllModels", Description = "Lists all Model")
    @PostMapping("/getAllModels")
    public PaginationResponse<Model> getAllModels(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ModelFilter modelFilter, @RequestAttribute SecurityContextBase securityContext) {
        service.validateFiltering(modelFilter, securityContext);
        return service.getAllModels(securityContext, modelFilter);
    }


    @PostMapping("/createModel")
    @Operation(summary = "createModel", description = "Creates Model")
    @IOperation(Name = "createModel", Description = "Creates Model")
    public Model createModel(
            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ModelCreate modelCreate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(modelCreate, securityContext);

        return service.createModel(modelCreate, securityContext);
    }


    @PutMapping("/updateModel")
    @Operation(summary = "updateModel", description = "Updates Model")
    @IOperation(Name = "updateModel", Description = "Updates Model")
    public Model updateModel(

            @RequestHeader(value = "authenticationKey", required = false) String key,
            @RequestBody ModelUpdate modelUpdate,
            @RequestAttribute SecurityContextBase securityContext) {
        service.validate(modelUpdate, securityContext);
        Model model = service.getByIdOrNull(modelUpdate.getId(),
                Model.class, Model_.security, securityContext);
        if (model == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no Model with id "
                    + modelUpdate.getId());
        }
        modelUpdate.setModel(model);

        return service.updateModel(modelUpdate, securityContext);
    }
}