package com.flexicore.product.iot.request;

import com.flexicore.interfaces.dynamic.FieldInfo;
import com.flexicore.product.containers.request.EquipmentCreate;

public class ExternalServerCreate extends EquipmentCreate {
    @FieldInfo
    private String url;


    public String getUrl() {
        return url;
    }

    public <T extends ExternalServerCreate> T setUrl(String url) {
        this.url = url;
        return (T) this;
    }


}
