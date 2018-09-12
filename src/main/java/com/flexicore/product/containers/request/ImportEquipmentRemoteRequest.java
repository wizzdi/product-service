package com.flexicore.product.containers.request;

import com.flexicore.product.model.ApiProvider;

public class ImportEquipmentRemoteRequest {


   private ApiProvider apiProvider;


    public ApiProvider getApiProvider() {
        return apiProvider;
    }

    public ImportEquipmentRemoteRequest setApiProvider(ApiProvider apiProvider) {
        this.apiProvider = apiProvider;
        return this;
    }
}
