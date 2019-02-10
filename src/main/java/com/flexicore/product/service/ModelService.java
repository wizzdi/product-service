package com.flexicore.product.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.model.Manufacturer;
import com.flexicore.product.data.ModelRepository;
import com.flexicore.product.interfaces.IModelService;
import com.flexicore.product.model.Model;
import com.flexicore.product.request.ModelCreate;
import com.flexicore.product.request.ModelFiltering;
import com.flexicore.product.request.ModelUpdate;
import com.flexicore.security.SecurityContext;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PluginInfo(version = 1)
public class ModelService implements IModelService {

    @Inject
    @PluginInfo(version = 1)
    private ModelRepository modelRepository;


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return modelRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, List<String> batchString, SecurityContext securityContext) {
        return modelRepository.getByIdOrNull(id, c, batchString, securityContext);
    }


    @Override
    public PaginationResponse<Model> getAllModels(ModelFiltering filtering, SecurityContext securityContext) {
        List<Model> list= modelRepository.getAllModels(filtering,securityContext);
        long count= modelRepository.countAllModels(filtering,securityContext);
        return new PaginationResponse<>(list,filtering,count);
    }




    @Override
    public Model createModel(ModelCreate modelCreate, SecurityContext securityContext) {
        Model model=createModelNoMerge(modelCreate,securityContext);
        modelRepository.merge(model);
        return model;
    }

    @Override
    public Model createModelNoMerge(ModelCreate modelCreate, SecurityContext securityContext) {
        Model model=Model.s().CreateUnchecked(modelCreate.getName(),securityContext);
        model.Init();
        updateModelNoMerge(model,modelCreate);
        return model;
    }

    @Override
    public boolean updateModelNoMerge(Model model, ModelCreate modelCreate) {
        boolean update=false;
        if(modelCreate.getName()!=null &&! modelCreate.getName().equals(model.getName())){
            model.setName(modelCreate.getName());
            update=true;
        }

        if(modelCreate.getDescription()!=null &&! modelCreate.getDescription().equals(model.getDescription())){
            model.setDescription(modelCreate.getDescription());
            update=true;
        }

        if(modelCreate.getManufacturer()!=null && (model.getManufacturer()==null || !modelCreate.getManufacturer().getId().equals(model.getManufacturer().getId()))){
            model.setManufacturer(modelCreate.getManufacturer());
            update=true;
        }
       return update;
    }


    @Override
    public void validateModelFiltering(ModelFiltering filtering, SecurityContext securityContext) {
        Set<String> ids = filtering.getManufacturersIds();
        List<Manufacturer> groups=ids.isEmpty()?new ArrayList<>():listByIds(Manufacturer.class, ids, securityContext);
        ids.removeAll(groups.parallelStream().map(f->f.getId()).collect(Collectors.toSet()));
        if(!ids.isEmpty()){
            throw new BadRequestException("could not find Manufacturer with ids "+ids.parallelStream().collect(Collectors.joining(",")));
        }
        filtering.setManufacturers(groups);

    }

    @Override
    public Model updateModel(ModelUpdate modelUpdate, SecurityContext securityContext) {
        Model model=modelUpdate.getModel();
        if(updateModelNoMerge(model,modelUpdate)){
            modelRepository.merge(model);
        }
        return model;
    }

    @Override
    public void validate(ModelCreate modelCreate, SecurityContext securityContext) {
        Manufacturer manufacturer=modelCreate.getManufacturerId()!=null?getByIdOrNull(modelCreate.getManufacturerId(),Manufacturer.class,null,securityContext):null;
        if(manufacturer==null){
            throw new BadRequestException("No Manufacturer with id "+modelCreate.getManufacturerId());
        }
        modelCreate.setManufacturer(manufacturer);
    }
}
