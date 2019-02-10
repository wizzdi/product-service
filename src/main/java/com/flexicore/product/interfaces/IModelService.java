package com.flexicore.product.interfaces;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.product.model.Model;
import com.flexicore.product.request.ModelCreate;
import com.flexicore.product.request.ModelFiltering;
import com.flexicore.product.request.ModelUpdate;
import com.flexicore.security.SecurityContext;

public interface IModelService {
    PaginationResponse<Model> getAllModels(ModelFiltering filtering, SecurityContext securityContext);

    Model createModel(ModelCreate modelCreate, SecurityContext securityContext);

    Model createModelNoMerge(ModelCreate modelCreate, SecurityContext securityContext);

    boolean updateModelNoMerge(Model model, ModelCreate modelCreate);

    void validateModelFiltering(ModelFiltering filtering, SecurityContext securityContext);

    Model updateModel(ModelUpdate modelUpdate, SecurityContext securityContext);

    void validate(ModelCreate modelCreate, SecurityContext securityContext);
}
