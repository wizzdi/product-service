package com.flexicore.product.interfaces;

import com.flexicore.code.metadata.model.Service;
import com.flexicore.code.metadata.model.UpdateNoMerge;
import com.flexicore.code.metadata.model.Validate;
import com.flexicore.code.metadata.model.ValidationType;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.product.model.Model;
import com.flexicore.product.request.ModelCreate;
import com.flexicore.product.request.ModelFiltering;
import com.flexicore.product.request.ModelUpdate;
import com.flexicore.security.SecurityContext;

@Service
public interface IModelService extends ServicePlugin {
    PaginationResponse<Model> getAllModels(ModelFiltering filtering, SecurityContext securityContext);

    Model createModel(ModelCreate modelCreate, SecurityContext securityContext);

    Model createModelNoMerge(ModelCreate modelCreate, SecurityContext securityContext);

    @UpdateNoMerge(type = Model.class)
    boolean updateModelNoMerge(Model model, ModelCreate modelCreate);

    @Validate(type = Model.class,validationTypes = ValidationType.FILTER)
    void validateModelFiltering(ModelFiltering filtering, SecurityContext securityContext);

    Model updateModel(ModelUpdate modelUpdate, SecurityContext securityContext);

    @Validate(type = Model.class,validationTypes = {ValidationType.UPDATE,ValidationType.CREATE})
    void validate(ModelCreate modelCreate, SecurityContext securityContext);
}
