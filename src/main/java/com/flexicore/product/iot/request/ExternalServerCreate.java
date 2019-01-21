package com.flexicore.product.iot.request;

import com.flexicore.product.containers.request.EquipmentCreate;

public class ExternalServerCreate extends EquipmentCreate {
    private String url;


    public String getUrl() {
        return url;
    }

    public <T extends ExternalServerCreate> T setUrl(String url) {
        this.url = url;
        return (T) this;
    }


}
