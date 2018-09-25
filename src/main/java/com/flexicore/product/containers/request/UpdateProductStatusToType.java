package com.flexicore.product.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;

public class UpdateProductStatusToType extends ProductStatusToTypeCreate {


    private String iconId;
    @JsonIgnore
    private FileResource icon;


    public String getIconId() {
        return iconId;
    }

    public UpdateProductStatusToType setIconId(String iconId) {
        this.iconId = iconId;
        return this;
    }

    @JsonIgnore
    public FileResource getIcon() {
        return icon;
    }

    public UpdateProductStatusToType setIcon(FileResource icon) {
        this.icon = icon;
        return this;
    }

}
